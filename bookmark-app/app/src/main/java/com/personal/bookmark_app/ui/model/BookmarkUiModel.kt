package com.personal.bookmark_app.ui.model

data class BookmarkUiModel(
    val id: Int,
    val source: BookmarkSource,
    val tags: List<String>,
    val date: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val originalUrl: String,
    val note: String? = null
)