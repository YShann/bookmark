package com.personal.bookmark_app.ui.model


data class FolderUiModel(
    val id: Long,
    val title: String,
    val count: Int,
    val imageUrl: String,
    val latestSavedAt: String?
)

