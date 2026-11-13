package com.personal.bookmark_app.ui.screen.searchbookmark

import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse

data class SearchBookmarkUiState(
    val query: String = "",
    val selectedSource: String? = null,
    val sourceCounts: Map<String, Long> = emptyMap(),
    val mutationError: String? = null,
    val moveSelection: BookmarkMoveSelection? = null
) {
    val totalCount: Long
        get() = sourceCounts.values.sum()
}

data class BookmarkMoveSelection(
    val bookmarkId: Long,
    val folders: List<FolderResponse>
)
