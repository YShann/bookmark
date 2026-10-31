package com.personal.bookmark_app.ui.model

data class FolderBookmarkUiModel(
    val id: String,
    val source: String,
    val date: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val tags: List<String>,
    val note: String? = null
)