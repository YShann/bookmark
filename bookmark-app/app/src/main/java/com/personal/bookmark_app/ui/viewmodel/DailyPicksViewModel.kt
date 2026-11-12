package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.DailyPicksApi
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.ui.model.DaliyBookmarkUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DailyPicksViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.authenticated(application).create(DailyPicksApi::class.java)

    private val _picks = MutableStateFlow<List<DaliyBookmarkUiModel>>(emptyList())
    val picks = _picks.asStateFlow()
    private val _totalBookmarkCount = MutableStateFlow(0L)
    val totalBookmarkCount = _totalBookmarkCount.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _selectionDate = MutableStateFlow("")
    val selectionDate = _selectionDate.asStateFlow()
    private val _isSufficient = MutableStateFlow(false)
    val isSufficient = _isSufficient.asStateFlow()



    fun refreshPicks() {
        if (_isLoading.value || _totalBookmarkCount.value <= 3) return
        loadPicks(refresh = true)
    }

    fun retry() = loadPicks()

    private fun loadPicks(refresh: Boolean = false) {
        if (_isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (refresh) api.refreshDailyPicks() else api.getDailyPicks()
                }
                _picks.value = response.orderedBookmarks().map { it.toDailyPick() }
                _totalBookmarkCount.value = response.bookmarkCount
                _selectionDate.value = response.selectionDate
                _isSufficient.value = response.isSufficient
                _error.value = null
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _error.value = "今日拾藏載入失敗，請稍後再試。"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

private fun BookmarkResponse.toDailyPick() = DaliyBookmarkUiModel(
    id = id,
    source = source,
    tags = tags,
    date = createdAt?.take(10)?.replace('-', '/') ?: "",
    title = title,
    description = content.orEmpty().ifBlank { url },
    imageUrl = imageUrl,
    originalUrl = url,
    note = note
)
