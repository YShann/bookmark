package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.response.user.UserProfileResponse
import retrofit2.http.GET
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST

interface UserApi {
    @GET("api/users/me")
    suspend fun getProfile(): UserProfileResponse

    @Multipart
    @POST("api/users/me/avatar")
    suspend fun updateAvatar(@Part file: MultipartBody.Part): UserProfileResponse
}
