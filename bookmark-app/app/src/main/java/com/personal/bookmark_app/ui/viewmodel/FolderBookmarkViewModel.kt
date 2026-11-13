package com.personal.bookmark_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.personal.bookmark_app.data.remote.PageLoader
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.api.FolderApi
import com.personal.bookmark_app.data.remote.api.FolderNameRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.MoveBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.toUpdateRequest
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse
import com.personal.bookmark_app.data.repository.FolderChanges
import com.personal.bookmark_app.ui.screen.folderbookmark.FolderBookmarkUiState
import com.personal.bookmark_app.ui.utils.MAX_BOOKMARK_TITLE_LENGTH
import com.personal.bookmark_app.ui.utils.MAX_TAG_NAME_LENGTH
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FolderBookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookmarkApi: BookmarkApi,
    private val folderApi: FolderApi,
    private val folderChanges: FolderChanges
) : ViewModel() {
    private val folderId = checkNotNull(savedStateHandle.get<Long>("folderId"))
    private val _state = MutableStateFlow(FolderBookmarkUiState())
    val uiState = _state.asStateFlow()

    private data class FolderFilter(
        val query: String = "",
        val tags: Set<String> = emptySet(),
        val newestFirst: Boolean = true
    )

    private var filter = FolderFilter()
    private var filterJob: Job? = null
    private var moveFoldersRequest: Deferred<List<FolderResponse>?>? = null
    private var moveFoldersLoaded = false
    private val pages = MutableStateFlow(createPages(filter))
    val bookmarks = pages.flatMapLatest { it.flow }.cachedIn(viewModelScope)

    init {
        loadFolderDetails()
        preloadMoveFolders()
    }

    private fun createPages(filter: FolderFilter) = PageLoader { page, size ->
        bookmarkApi.getFolderBookmarkPage(
            folderId,
            filter.query,
            filter.tags.toList(),
            filter.newestFirst,
            page,
            size
        )
            .also {
                if (this.filter === filter) _state.value =
                    _state.value.copy(totalCount = it.totalElements)
            }
    }

    fun onQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        scheduleFilter()
    }

    fun toggleTag(tag: String) {
        val selected = _state.value.selectedTags
        _state.value =
            _state.value.copy(selectedTags = if (tag in selected) selected - tag else selected + tag)
        scheduleFilter()
    }

    fun clearTags() {
        _state.value = _state.value.copy(selectedTags = emptySet())
        scheduleFilter()
    }

    fun toggleSortOrder() {
        _state.value = _state.value.copy(isNewestFirst = !_state.value.isNewestFirst)
        scheduleFilter()
    }

    private fun scheduleFilter() {
        filterJob?.cancel()
        val state = _state.value
        val nextFilter = FolderFilter(state.searchQuery, state.selectedTags, state.isNewestFirst)
        if (nextFilter == filter) return
        filterJob = viewModelScope.launch {
            delay(250)
            filter = nextFilter
            _state.value = _state.value.copy(totalCount = 0)
            pages.value = createPages(nextFilter)
        }
    }

    private fun updateAvailableTags(tags: List<String>) {
        val selected = _state.value.selectedTags
        val remaining = selected.intersect(tags.toSet())
        _state.value = _state.value.copy(tags = tags, selectedTags = remaining)
        if (remaining != selected) scheduleFilter()
    }

    fun refresh() {
        if (_state.value.isLoading || _state.value.isMutating) return
        preloadMoveFolders()
        pages.value.refresh()
        loadFolderDetails()
    }

    private fun loadFolderDetails() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val folder = folderApi.getFolder(folderId)
                val tags = folderApi.getTags(folderId).map { it.name }.sorted()
                _state.value = _state.value.copy(folderName = folder.name)
                updateAvailableTags(tags)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(error = "載入收藏夾失敗，請重試")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun preloadMoveFolders() {
        moveFoldersLoaded = false
        moveFoldersRequest?.cancel()
        moveFoldersRequest = viewModelScope.async {
            try {
                folderApi.getFolders()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                null
            }
        }
    }

    fun loadMoveFolders(onLoaded: () -> Unit) {
        if (_state.value.isLoading || _state.value.isMutating) return
        if (moveFoldersLoaded) {
            onLoaded()
            return
        }

        mutate {
            val folders = moveFoldersRequest?.await()
            moveFoldersRequest = null
            _state.value = _state.value.copy(folders = folders ?: folderApi.getFolders())
            moveFoldersLoaded = true
            onLoaded()
        }
    }

    fun isBookmarkFilterApplied(): Boolean {
        val state = _state.value
        return filter.query == state.searchQuery && filter.tags == state.selectedTags &&
                filter.newestFirst == state.isNewestFirst
    }

    fun getMatchingIds(onLoaded: (Set<String>) -> Unit) = mutate {
        val state = _state.value
        val ids = bookmarkApi.getMatchingFolderBookmarkIds(
            folderId,
            state.searchQuery,
            state.selectedTags.toList()
        )
        if (_state.value.searchQuery == state.searchQuery && _state.value.selectedTags == state.selectedTags) {
            onLoaded(ids.map { it.toString() }.toSet())
        }
    }

    private fun mutate(action: suspend () -> Unit) {
        if (_state.value.isLoading || _state.value.isMutating) return
        _state.value = _state.value.copy(isMutating = true, error = null)
        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(error = "操作失敗，未完成的收藏已保留，請重試")
            } finally {
                _state.value = _state.value.copy(isMutating = false)
            }
        }
    }

    fun update(
        old: BookmarkResponse,
        title: String? = null,
        note: String? = null,
        tags: List<String>? = null
    ) {
        if (title != null && (title.isBlank() || title.trim().length > MAX_BOOKMARK_TITLE_LENGTH) ||
            tags != null && tags.any { it.isBlank() || it.length > MAX_TAG_NAME_LENGTH }
        ) {
            _state.value = _state.value.copy(
                error = "標題須為 1–$MAX_BOOKMARK_TITLE_LENGTH 字，標籤須為 1–$MAX_TAG_NAME_LENGTH 字"
            )
            return
        }
        mutate {
            bookmarkApi.updateBookmark(old.id, old.toUpdateRequest(title?.trim(), note, tags))
            refreshAfterMutation()
        }
    }

    fun delete(ids: List<String>, onRemoved: (Set<String>) -> Unit, onSuccess: () -> Unit = {}) =
        mutate {
            processBatch(ids, onRemoved, onSuccess) { bookmarkApi.deleteBookmark(it) }
        }

    fun move(
        ids: List<String>,
        destination: Long,
        onRemoved: (Set<String>) -> Unit,
        onSuccess: () -> Unit
    ) = mutate {
        if (destination == folderId || _state.value.folders.none { it.id == destination }) return@mutate
        processBatch(ids, onRemoved, onSuccess) {
            bookmarkApi.moveBookmark(
                it,
                MoveBookmarkRequest(destination)
            )
        }
    }

    private suspend fun processBatch(
        ids: List<String>,
        onRemoved: (Set<String>) -> Unit,
        onSuccess: () -> Unit,
        action: suspend (Long) -> Unit
    ) {
        var failures = 0
        val removed = mutableSetOf<String>()
        for (id in ids.distinct()) {
            try {
                action(id.toLong())
                removed += id
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                failures++
            }
        }
        onRemoved(removed)
        if (failures == 0) onSuccess()
        else _state.value = _state.value.copy(error = "$failures 筆收藏操作失敗，已保留供重試")
        if (removed.isNotEmpty()) refreshAfterMutation()
    }

    private suspend fun refreshAfterMutation() {
        folderChanges.notifyChanged()
        pages.value.refresh()
        try {
            val tags = folderApi.getTags(folderId).map { it.name }.sorted()
            updateAvailableTags(tags)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            if (_state.value.error == null) _state.value =
                _state.value.copy(error = "載入收藏夾失敗，請重試")
        }
    }

    suspend fun createFolder(name: String) {
        check(!_state.value.isLoading && !_state.value.isMutating)
        _state.value = _state.value.copy(isMutating = true, error = null)
        try {
            val folder = folderApi.createFolder(FolderNameRequest(name.trim()))
            folderChanges.notifyChanged()
            _state.value = _state.value.copy(folders = _state.value.folders + folder)
        } finally {
            _state.value = _state.value.copy(isMutating = false)
        }
    }
}
