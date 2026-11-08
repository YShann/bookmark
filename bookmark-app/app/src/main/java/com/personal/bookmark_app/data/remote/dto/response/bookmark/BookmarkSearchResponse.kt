package com.personal.bookmark_app.data.remote.dto.response.bookmark

data class BookmarkSearchResponse(
    val content: List<BookmarkResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val sourceCounts: Map<String, Long>
)
