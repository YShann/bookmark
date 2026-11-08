package com.personal.bookmark_app.data.remote.dto.response.bookmark

data class DailyPicksResponse(
    val totalBookmarkCount: Long,
    val picks: List<BookmarkResponse>
)
