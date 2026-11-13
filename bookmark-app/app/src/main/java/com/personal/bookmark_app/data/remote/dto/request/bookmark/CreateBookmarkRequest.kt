package com.personal.bookmark_app.data.remote.dto.request.bookmark

data class CreateBookmarkRequest(
    val url: String,
    val title: String,
    val note: String?,
    val source: String,
    val tags: List<String>,
    val content: String? = null,
    val imageUrl: String? = null,
    val addToDailyPicks: Boolean = false
)
