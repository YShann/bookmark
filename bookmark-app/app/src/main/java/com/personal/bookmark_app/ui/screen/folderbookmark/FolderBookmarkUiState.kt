package com.personal.bookmark_app.ui.screen.folderbookmark

import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse

data class FolderBookmarkUiState(
    val folderName: String = "收藏夾",
    val tags: List<String> = emptyList(),
    val totalCount: Long = 0,
    val folders: List<FolderResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isMutating: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedTags: Set<String> = emptySet(),
    val isNewestFirst: Boolean = true
)
