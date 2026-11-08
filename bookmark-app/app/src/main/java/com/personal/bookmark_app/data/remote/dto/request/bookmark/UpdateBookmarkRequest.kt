package com.personal.bookmark_app.data.remote.dto.request.bookmark

data class UpdateBookmarkRequest(
    val url: String,
    val title: String,
    val content: String?,
    val note: String?,
    val source: String,
    val imageUrl: String?,
    val tags: List<String>
)
