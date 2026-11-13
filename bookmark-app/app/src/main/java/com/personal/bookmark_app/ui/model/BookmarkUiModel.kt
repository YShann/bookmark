package com.personal.bookmark_app.ui.model

import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.displayNameFor

data class BookmarkUiModel(
    val id: String,
    val source: String,
    val date: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val tags: List<String>,
    val note: String? = null
)

internal fun BookmarkResponse.toUiModel(bookmarkSources: List<BookmarkSourceResponse>) =
    BookmarkUiModel(
        id = id.toString(),
        source = bookmarkSources.displayNameFor(source),
        date = createdAt?.take(10).orEmpty(),
        title = title,
        description = content.orEmpty().ifBlank { url },
        imageUrl = imageUrl.orEmpty(),
        tags = tags,
        note = note
    )
