package com.personal.bookmark_app.data.remote.dto.response.bookmark

import com.google.gson.annotations.SerializedName

data class DailyPicksResponse(
    val selectionDate: String,
    val bookmarkCount: Long,
    @SerializedName("isSufficient") val isSufficient: Boolean,
    val dailyPicks: List<DailyPickEntry>
) {
    fun orderedBookmarks(): List<BookmarkResponse> =
        if (isSufficient) dailyPicks.sortedBy { it.position }.map { it.bookmark } else emptyList()
}

data class DailyPickEntry(val position: Int, val bookmark: BookmarkResponse)
