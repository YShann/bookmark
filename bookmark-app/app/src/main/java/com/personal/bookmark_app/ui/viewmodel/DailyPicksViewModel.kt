package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.ui.model.BookmarkSource
import com.personal.bookmark_app.ui.model.DaliyBookmarkUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DailyPicksViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.authenticated(application).create(BookmarkApi::class.java)

    private val _picks = MutableStateFlow<List<DaliyBookmarkUiModel>>(emptyList())
    val picks = _picks.asStateFlow()
    private val _totalBookmarkCount = MutableStateFlow(0L)
    val totalBookmarkCount = _totalBookmarkCount.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var rotation = 0

    init { loadPicks() }

    fun refreshPicks() {
        if (_isLoading.value || _totalBookmarkCount.value <= 3) return
        rotation++
        loadPicks(excludeCurrent = true)
    }

    fun retry() = loadPicks()

    private fun loadPicks(excludeCurrent: Boolean = false) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getDailyPicks(
                        rotation = rotation,
                        excludeIds = if (excludeCurrent) _picks.value.map { it.id } else emptyList()
                    )
                }
                _picks.value = response.picks.map { it.toDailyPick() }
                _totalBookmarkCount.value = response.totalBookmarkCount
                _error.value = null
            } catch (_: Exception) {
                _error.value = "今日拾藏載入失敗，請稍後再試。"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

private fun BookmarkResponse.toDailyPick() = DaliyBookmarkUiModel(
    id = id,
    source = when (source?.uppercase()) {
        "THREADS" -> BookmarkSource.THREADS
        "INSTAGRAM" -> BookmarkSource.INSTAGRAM
        "DCARD" -> BookmarkSource.DCARD
        "YOUTUBE" -> BookmarkSource.YOUTUBE
        else -> BookmarkSource.WEB
    },
    tags = tags,
    date = createdAt?.take(10)?.replace('-', '/') ?: "",
    title = title,
    description = content.orEmpty().ifBlank { url },
    imageUrl = imageUrl,
    originalUrl = url,
    note = note
)
