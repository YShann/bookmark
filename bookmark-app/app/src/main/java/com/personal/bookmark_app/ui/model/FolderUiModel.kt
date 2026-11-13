package com.personal.bookmark_app.ui.model

import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse

data class FolderUiModel(
    val id: Long,
    val title: String,
    val count: Int,
    val imageUrl: String,
    val latestSavedAt: String?
)

internal fun FolderResponse.toUiModel() = FolderUiModel(
    id = id,
    title = name,
    count = bookmarkCount,
    imageUrl = imageUrl.orEmpty(),
    latestSavedAt = null
)

