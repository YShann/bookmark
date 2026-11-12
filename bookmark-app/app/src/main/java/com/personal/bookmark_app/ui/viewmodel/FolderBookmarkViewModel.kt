package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.api.FolderApi
import com.personal.bookmark_app.data.remote.api.FolderNameRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.MoveBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.UpdateBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.OTHER_SOURCE_CODE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FolderBookmarkDataState(
    val folderName: String = "收藏夾",
    val bookmarks: List<BookmarkResponse> = emptyList(),
    val folders: List<FolderResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isMutating: Boolean = false,
    val error: String? = null
)

class FolderBookmarkViewModel(application: Application, savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val folderId = checkNotNull(savedStateHandle.get<Long>("folderId"))
    private val retrofit = RetrofitClient.authenticated(application)
    private val bookmarkApi = retrofit.create(BookmarkApi::class.java)
    private val folderApi = retrofit.create(FolderApi::class.java)
    private val _state = MutableStateFlow(FolderBookmarkDataState())
    val state = _state.asStateFlow()

    fun refresh() {
        if (_state.value.isLoading || _state.value.isMutating) return
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                coroutineScope {
                    val bookmarks = async { bookmarkApi.getFolderBookmarks(folderId) }
                    val folders = folderApi.getFolders()
                    _state.value = _state.value.copy(folderName = folders.firstOrNull { it.id == folderId }?.name ?: "收藏夾",
                        folders = folders)
                    _state.value = _state.value.copy(bookmarks = bookmarks.await())
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(error = "載入收藏夾失敗，請重試")
            } finally { _state.value = _state.value.copy(isLoading = false) }
        }
    }

    private fun mutate(action: suspend () -> Unit) {
        if (_state.value.isLoading || _state.value.isMutating) return
        _state.value = _state.value.copy(isMutating = true, error = null)
        viewModelScope.launch {
            try { action() } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(error = "操作失敗，未完成的收藏已保留，請重試")
            } finally { _state.value = _state.value.copy(isMutating = false) }
        }
    }

    fun update(id: String, title: String? = null, note: String? = null, tags: List<String>? = null) {
        if (title != null && (title.isBlank() || title.trim().length > 255) ||
            tags != null && tags.any { it.isBlank() || it.length > 30 }) {
            _state.value = _state.value.copy(error = "標題須為 1–255 字，標籤須為 1–30 字")
            return
        }
        mutate {
            val old = _state.value.bookmarks.firstOrNull { it.id.toString() == id } ?: return@mutate
            val updated = bookmarkApi.updateBookmark(old.id, UpdateBookmarkRequest(
                url = old.url, title = title?.trim() ?: old.title, content = old.content,
                note = note ?: old.note, source = old.source ?: OTHER_SOURCE_CODE,
                imageUrl = old.imageUrl, tags = tags ?: old.tags))
            _state.value = _state.value.copy(bookmarks = _state.value.bookmarks.map { if (it.id == old.id) updated else it })
        }
    }

    fun delete(ids: List<String>, onSuccess: () -> Unit = {}) = mutate {
        processBatch(ids, onSuccess) { bookmarkApi.deleteBookmark(it) }
    }

    fun move(ids: List<String>, destination: Long, onSuccess: () -> Unit) = mutate {
        if (destination == folderId || _state.value.folders.none { it.id == destination }) return@mutate
        processBatch(ids, onSuccess) { bookmarkApi.moveBookmark(it, MoveBookmarkRequest(destination)) }
    }

    private suspend fun processBatch(ids: List<String>, onSuccess: () -> Unit, action: suspend (Long) -> Unit) {
        var failures = 0
        for (id in ids.distinct()) {
            val bookmark = _state.value.bookmarks.firstOrNull { it.id.toString() == id } ?: continue
            try {
                action(bookmark.id)
                _state.value = _state.value.copy(bookmarks = _state.value.bookmarks.filterNot { it.id == bookmark.id })
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                failures++
            }
        }
        if (failures == 0) onSuccess()
        else _state.value = _state.value.copy(error = "$failures 筆收藏操作失敗，已保留供重試")
    }

    suspend fun createFolder(name: String) {
        check(!_state.value.isLoading && !_state.value.isMutating)
        _state.value = _state.value.copy(isMutating = true, error = null)
        try {
            val folder = folderApi.createFolder(FolderNameRequest(name.trim()))
            _state.value = _state.value.copy(folders = _state.value.folders + folder)
        } finally {
            _state.value = _state.value.copy(isMutating = false)
        }
    }
}
