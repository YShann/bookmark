package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.dto.request.bookmark.UpdateBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.OTHER_SOURCE_CODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

data class SearchBookmarkEntry(val bookmark: BookmarkResponse)

class SearchBookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val bookmarkApi = RetrofitClient.authenticated(application).create(BookmarkApi::class.java)

    private val _bookmarks = MutableStateFlow<List<SearchBookmarkEntry>>(emptyList())
    val bookmarks = _bookmarks.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _sourceCounts = MutableStateFlow<Map<String, Long>>(emptyMap())
    val sourceCounts = _sourceCounts.asStateFlow()
    private val _totalCount = MutableStateFlow(0L)
    val totalCount = _totalCount.asStateFlow()

    private var currentPage = 0
    private var totalPages = 0
    private var query = ""
    private var source: String? = null
    private var searchJob: Job? = null
    private var isLoadingNextPage = false
    private var searchGeneration = 0

    fun search(newQuery: String = query, newSource: String? = source) {
        query = newQuery.trim()
        source = newSource
        searchJob?.cancel()
        val generation = ++searchGeneration
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            isLoadingNextPage = false
            try {
                val result = withContext(Dispatchers.IO) {
                    bookmarkApi.searchBookmarks(
                        query = query.ifBlank { null },
                        source = source,
                        page = 0
                    )
                }
                _bookmarks.value = result.content.map(::SearchBookmarkEntry)
                currentPage = result.page
                totalPages = result.totalPages
                _totalCount.value = result.sourceCounts.values.sum()
                _sourceCounts.value = result.sourceCounts
                _error.value = null
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (generation == searchGeneration) _error.value = "搜尋收藏載入失敗，請稍後再試。"
            } finally {
                if (generation == searchGeneration) _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        if (_isLoading.value || isLoadingNextPage || currentPage + 1 >= totalPages) return
        isLoadingNextPage = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    bookmarkApi.searchBookmarks(
                        query = query.ifBlank { null },
                        source = source,
                        page = currentPage + 1
                    )
                }
                _bookmarks.value = _bookmarks.value + result.content.map(::SearchBookmarkEntry)
                currentPage = result.page
                totalPages = result.totalPages
                _error.value = null
            } catch (_: Exception) {
                _error.value = "載入更多收藏失敗，請稍後再試。"
            } finally {
                _isLoading.value = false
                isLoadingNextPage = false
            }
        }
    }

    fun updateBookmark(entry: SearchBookmarkEntry, title: String? = null, note: String? = null, tags: List<String>? = null) {
        val old = entry.bookmark
        viewModelScope.launch {
            try {
                val updated = withContext(Dispatchers.IO) {
                    bookmarkApi.updateBookmark(old.id, UpdateBookmarkRequest(
                        url = old.url,
                        title = title ?: old.title,
                        content = old.content,
                        note = note ?: old.note,
                        source = old.source ?: OTHER_SOURCE_CODE,
                        imageUrl = old.imageUrl,
                        tags = tags ?: old.tags
                    ))
                }
                _bookmarks.value = _bookmarks.value.map {
                    if (it.bookmark.id == old.id) it.copy(bookmark = updated) else it
                }
                _error.value = null
            } catch (_: Exception) {
                _error.value = "收藏更新失敗，請稍後再試。"
            }
        }
    }

    fun deleteBookmark(entry: SearchBookmarkEntry) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { bookmarkApi.deleteBookmark(entry.bookmark.id) }
                val visibleBookmarks = _bookmarks.value
                _bookmarks.value = visibleBookmarks.filterNot { it.bookmark.id == entry.bookmark.id }
                if (_bookmarks.value.size < visibleBookmarks.size) {
                    _totalCount.value = (_totalCount.value - 1).coerceAtLeast(0)
                    val storedSource = entry.bookmark.source
                    val deletedSource = _sourceCounts.value.keys.firstOrNull {
                        it.equals(storedSource, ignoreCase = true)
                    }
                    if (deletedSource != null) {
                        val counts = _sourceCounts.value
                        val remaining = ((counts[deletedSource] ?: 0L) - 1).coerceAtLeast(0)
                        _sourceCounts.value = counts + (deletedSource to remaining)
                    }
                }
                _error.value = null
            } catch (_: Exception) {
                _error.value = "收藏刪除失敗，請稍後再試。"
            }
        }
    }
}
