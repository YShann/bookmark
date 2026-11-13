package com.personal.bookmark_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.api.DailyPicksApi
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.ui.model.DaliyBookmarkUiModel
import com.personal.bookmark_app.ui.screen.dailypicks.DailyPicksUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

@HiltViewModel
class DailyPicksViewModel @Inject constructor(
    private val api: DailyPicksApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyPicksUiState())
    val uiState = _uiState.asStateFlow()

    fun refreshPicks() {
        if (!_uiState.value.canRefresh) return
        loadPicks(refresh = true)
    }

    fun retry() = loadPicks()

    suspend fun watchDailyRefresh() {
        val taipei = ZoneId.of("Asia/Taipei")
        while (true) {
            val now = ZonedDateTime.now(taipei)
            val morning = now.toLocalDate().atTime(6, 0).atZone(taipei)
            val nextRefresh = if (now.isBefore(morning)) morning else morning.plusDays(1)
            delay(Duration.between(now, nextRefresh).toMillis() + 1)
            // 若手動換組仍在進行，等它完成再讀取新一天的結果。
            uiState.first { !it.isLoading }
            retry()
        }
    }

    private fun loadPicks(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (refresh) api.refreshDailyPicks() else api.getDailyPicks()
                }
                _uiState.update {
                    it.copy(
                        picks = response.orderedBookmarks()
                            .map { bookmark -> bookmark.toDailyPick() },
                        totalBookmarkCount = response.bookmarkCount,
                        selectionDate = response.selectionDate,
                        isSufficient = response.isSufficient
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { it.copy(error = "今日拾藏載入失敗，請稍後再試。") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
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
