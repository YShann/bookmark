package com.personal.bookmark_app.data.remote.dto.response.bookmark

internal const val OTHER_SOURCE_CODE = "OTHER"

data class BookmarkSourceResponse(
    val code: String,
    val displayName: String
)

fun List<BookmarkSourceResponse>.displayNameFor(code: String?): String {
    val sourceCode = code ?: OTHER_SOURCE_CODE
    return firstOrNull { it.code.equals(sourceCode, ignoreCase = true) }?.displayName ?: sourceCode
}
