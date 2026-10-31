package com.personal.bookmark_app.ui.screen.addbookmark

import com.personal.bookmark_app.ui.model.BookmarkSource

/**
 * 文章預覽資料
 */
data class BookmarkPreviewUiModel(
    val imageUrl: String,
    val title: String,
    val sourceUrl: String,
    val source: BookmarkSource,
    val isParsed: Boolean = true
)

/**
 * 索引標籤選項
 */
data class TagOptionUiModel(
    val id: String,
    val name: String
)