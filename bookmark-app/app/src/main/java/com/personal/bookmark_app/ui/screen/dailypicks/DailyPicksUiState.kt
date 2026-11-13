package com.personal.bookmark_app.ui.screen.dailypicks

import com.personal.bookmark_app.ui.model.DaliyBookmarkUiModel

data class DailyPicksUiState(
    val picks: List<DaliyBookmarkUiModel> = emptyList(),
    val totalBookmarkCount: Long = 0,
    val selectionDate: String? = null,
    val isSufficient: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canRefresh: Boolean
        get() = totalBookmarkCount > 3 && !isLoading
}
