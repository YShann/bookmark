package com.personal.bookmark_app.ui.screen.folderdetail

data class FolderDetailUiState(
    val searchQuery: String = "",
    val selectedTags: Set<String> = emptySet(),
    val isTagFilterExpanded: Boolean = true,
    val isNewestFirst: Boolean = true,
    val isBatchManaging: Boolean = false
)
