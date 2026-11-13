package com.personal.bookmark_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.personal.bookmark_app.data.remote.PageLoader
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.api.FolderApi
import com.personal.bookmark_app.data.remote.api.FolderNameRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.MoveBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.toUpdateRequest
import com.personal.bookmark_app.data.remote.dto.response.PageResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.repository.FolderChanges
import com.personal.bookmark_app.ui.screen.searchbookmark.BookmarkMoveSelection
import com.personal.bookmark_app.ui.screen.searchbookmark.SearchBookmarkUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SearchBookmarkViewModel @Inject constructor(
    private val bookmarkApi: BookmarkApi,
    private val folderApi: FolderApi,
    private val folderChanges: FolderChanges
) : ViewModel() {
    private data class SearchFilter(val query: String = "", val source: String? = null)

    private val _uiState = MutableStateFlow(SearchBookmarkUiState())
    val uiState = _uiState.asStateFlow()

    // 關鍵字等使用者停下輸入後再查詢，平台切換則立即套用。
    private var appliedFilter = SearchFilter()
    private var searchJob: Job? = null
    private var loadMoveFoldersJob: Job? = null
    private val pages = MutableStateFlow(createPages(appliedFilter))
    val bookmarks = pages.flatMapLatest { it.flow }.cachedIn(viewModelScope)

    private fun createPages(filter: SearchFilter) = PageLoader { page, size ->
        val response =
            bookmarkApi.searchBookmarks(filter.query.ifBlank { null }, filter.source, page, size)
        if (appliedFilter === filter) {
            _uiState.update { it.copy(sourceCounts = response.sourceCounts) }
        }
        PageResponse(response.content, response.page, response.totalPages, response.totalElements)
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        scheduleSearch()
    }

    fun onSourceSelected(source: String?) {
        if (source == _uiState.value.selectedSource) return
        searchJob?.cancel()
        _uiState.update { it.copy(selectedSource = source) }
        applySearchFilter()
    }

    private fun scheduleSearch() {
        searchJob?.cancel()
        val state = _uiState.value
        val nextFilter = SearchFilter(state.query.trim(), state.selectedSource)
        if (nextFilter == appliedFilter) return

        searchJob = viewModelScope.launch {
            delay(300)
            applySearchFilter()
        }
    }

    private fun applySearchFilter() {
        val state = _uiState.value
        val nextFilter = SearchFilter(state.query.trim(), state.selectedSource)
        if (nextFilter == appliedFilter) return

        appliedFilter = nextFilter
        _uiState.update { it.copy(mutationError = null) }
        pages.value = createPages(nextFilter)
    }

    fun updateBookmark(
        bookmark: BookmarkResponse,
        title: String? = null,
        note: String? = null,
        tags: List<String>? = null
    ) {
        changeBookmark("收藏更新失敗，請稍後再試。") {
            bookmarkApi.updateBookmark(bookmark.id, bookmark.toUpdateRequest(title, note, tags))
        }
    }

    fun deleteBookmark(bookmark: BookmarkResponse) {
        changeBookmark("收藏刪除失敗，請稍後再試。") {
            bookmarkApi.deleteBookmark(bookmark.id)
        }
    }

    fun openMoveSheet(bookmark: BookmarkResponse) {
        loadMoveFoldersJob?.cancel()
        loadMoveFoldersJob = viewModelScope.launch {
            try {
                val folders = folderApi.getFolders().filterNot { it.id == bookmark.folderId }
                _uiState.update {
                    it.copy(
                        moveSelection = BookmarkMoveSelection(bookmark.id, folders),
                        mutationError = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { it.copy(mutationError = "載入收藏夾失敗，請重試") }
            }
        }
    }

    fun closeMoveSheet() {
        _uiState.update { it.copy(moveSelection = null) }
    }

    fun moveBookmark(destination: Long) {
        val selection = _uiState.value.moveSelection ?: return
        if (selection.folders.none { it.id == destination }) return
        closeMoveSheet()
        changeBookmark("移動收藏失敗，請稍後再試。") {
            bookmarkApi.moveBookmark(selection.bookmarkId, MoveBookmarkRequest(destination))
        }
    }

    suspend fun createMoveFolder(name: String) {
        val selection = checkNotNull(_uiState.value.moveSelection)
        val folder = folderApi.createFolder(FolderNameRequest(name.trim()))
        folderChanges.notifyChanged()
        _uiState.update { state ->
            if (state.moveSelection == selection) {
                state.copy(moveSelection = selection.copy(folders = selection.folders + folder))
            } else {
                state
            }
        }
    }

    private fun changeBookmark(errorMessage: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                folderChanges.notifyChanged()
                _uiState.update { it.copy(mutationError = null) }
                pages.value.refresh()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { it.copy(mutationError = errorMessage) }
            }
        }
    }
}
