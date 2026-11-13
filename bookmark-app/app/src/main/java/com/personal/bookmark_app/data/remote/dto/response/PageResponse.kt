package com.personal.bookmark_app.data.remote.dto.response

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val totalPages: Int,
    val totalElements: Long
)
