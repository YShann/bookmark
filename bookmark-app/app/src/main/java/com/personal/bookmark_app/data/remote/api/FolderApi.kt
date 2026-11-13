package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.response.PageResponse

import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FolderApi {
    @GET("api/folders")
    suspend fun getFolders(): List<FolderResponse>

    @GET("api/folders/page")
    suspend fun getFolderPage(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageResponse<FolderResponse>

    @GET("api/folders/{folderId}")
    suspend fun getFolder(@Path("folderId") folderId: Long): FolderResponse

    @GET("api/folders/{folderId}/tags")
    suspend fun getTags(@Path("folderId") folderId: Long): List<FolderTagResponse>

    @POST("api/folders")
    suspend fun createFolder(@Body request: FolderNameRequest): FolderResponse

    @PATCH("api/folders/{folderId}")
    suspend fun renameFolder(
        @Path("folderId") folderId: Long,
        @Body request: FolderNameRequest
    ): FolderResponse

    @DELETE("api/folders/{folderId}")
    suspend fun deleteFolder(
        @Path("folderId") folderId: Long,
        @Query("deleteBookmarks") deleteBookmarks: Boolean = false
    )
}

data class FolderNameRequest(val name: String)

data class FolderTagResponse(val id: Long, val name: String)
