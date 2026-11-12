package com.personal.bookmark_app.ui.model

data class DaliyBookmarkUiModel(
    val id: Long,
    val source: String?,
    val tags: List<String>,
    val date: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val originalUrl: String,
    val note: String? = null
)
