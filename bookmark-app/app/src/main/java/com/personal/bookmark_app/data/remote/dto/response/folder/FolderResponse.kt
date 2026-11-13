package com.personal.bookmark_app.data.remote.dto.response.folder

data class FolderResponse(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val isDefault: Boolean,
    val bookmarkCount: Int = 0
)
