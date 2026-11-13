package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.request.bookmark.CreateBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.MoveBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.request.bookmark.UpdateBookmarkRequest
import com.personal.bookmark_app.data.remote.dto.response.PageResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSearchResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookmarkApi {
    @GET("api/bookmark-sources")
    suspend fun getBookmarkSources(): List<BookmarkSourceResponse>

    @POST("api/folders/{folderId}/bookmarks")
    suspend fun createBookmark(
        @Path("folderId") folderId: Long,
        @Body request: CreateBookmarkRequest
    ): BookmarkResponse

    @GET("api/folders/{folderId}/bookmarks/page")
    suspend fun getFolderBookmarkPage(
        @Path("folderId") folderId: Long,
        @Query("query") query: String,
        @Query("tag") tags: List<String>,
        @Query("newestFirst") newestFirst: Boolean,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageResponse<BookmarkResponse>

    @GET("api/folders/{folderId}/bookmarks/ids")
    suspend fun getMatchingFolderBookmarkIds(
        @Path("folderId") folderId: Long,
        @Query("query") query: String,
        @Query("tag") tags: List<String>
    ): List<Long>

    @GET("api/bookmarks/search")
    suspend fun searchBookmarks(
        @Query("query") query: String?,
        @Query("source") source: String?,
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): BookmarkSearchResponse

    @PATCH("api/bookmarks/{bookmarkId}")
    suspend fun updateBookmark(
        @Path("bookmarkId") bookmarkId: Long,
        @Body request: UpdateBookmarkRequest
    ): BookmarkResponse

    @PATCH("api/bookmarks/{bookmarkId}/move")
    suspend fun moveBookmark(
        @Path("bookmarkId") bookmarkId: Long,
        @Body request: MoveBookmarkRequest
    ): BookmarkResponse

    @DELETE("api/bookmarks/{bookmarkId}")
    suspend fun deleteBookmark(@Path("bookmarkId") bookmarkId: Long)
}

