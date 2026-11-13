package com.personal.bookmark_app.data.remote.dto.request.bookmark

import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.OTHER_SOURCE_CODE

data class UpdateBookmarkRequest(
    val url: String,
    val title: String,
    val content: String?,
    val note: String?,
    val source: String,
    val imageUrl: String?,
    val tags: List<String>
)

// 只替換這次編輯的欄位，其餘沿用原本的收藏資料。
internal fun BookmarkResponse.toUpdateRequest(
    title: String?,
    note: String?,
    tags: List<String>?
) = UpdateBookmarkRequest(
    url = url,
    title = title ?: this.title,
    content = content,
    note = note ?: this.note,
    source = source ?: OTHER_SOURCE_CODE,
    imageUrl = imageUrl,
    tags = tags ?: this.tags
)
