package com.personal.bookmark_app.ui.screen.folderbookmark

data class FolderBookmarkUiState(
    val searchQuery: String = "",
    val selectedTags: Set<String> = emptySet(),
    val isTagFilterExpanded: Boolean = false,
    val isNewestFirst: Boolean = true,
    val isBatchManaging: Boolean = false
)
