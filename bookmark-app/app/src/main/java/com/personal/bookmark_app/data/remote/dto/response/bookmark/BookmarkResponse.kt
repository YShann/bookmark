package com.personal.bookmark_app.data.remote.dto.response.bookmark

data class BookmarkResponse(
    val id: Long,
    val folderId: Long,
    val url: String,
    val title: String,
    val content: String?,
    val note: String?,
    val source: String?,
    val imageUrl: String?,
    val tags: List<String>,
    val createdAt: String?,
    val updatedAt: String?
)
