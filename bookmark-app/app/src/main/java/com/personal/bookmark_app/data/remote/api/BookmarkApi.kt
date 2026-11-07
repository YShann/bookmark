package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BookmarkApi {
    @GET("api/folders/{folderId}/bookmarks")
    suspend fun getFolderBookmarks(@Path("folderId") folderId: Long): List<BookmarkResponse>
}

