package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSearchResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.DailyPicksResponse
import com.personal.bookmark_app.data.remote.dto.request.bookmark.UpdateBookmarkRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface BookmarkApi {
    @GET("api/folders/{folderId}/bookmarks")
    suspend fun getFolderBookmarks(@Path("folderId") folderId: Long): List<BookmarkResponse>

    @GET("api/bookmarks/search")
    suspend fun searchBookmarks(
        @Query("query") query: String?,
        @Query("source") source: String?,
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): BookmarkSearchResponse

    @GET("api/bookmarks/daily-picks")
    suspend fun getDailyPicks(
        @Query("rotation") rotation: Int,
        @Query("excludeIds") excludeIds: List<Long> = emptyList()
    ): DailyPicksResponse

    @PATCH("api/bookmarks/{bookmarkId}")
    suspend fun updateBookmark(
        @Path("bookmarkId") bookmarkId: Long,
        @Body request: UpdateBookmarkRequest
    ): BookmarkResponse

    @DELETE("api/bookmarks/{bookmarkId}")
    suspend fun deleteBookmark(@Path("bookmarkId") bookmarkId: Long)
}

