package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.request.auth.EmailRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.LoginRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.RegisterRequest
import com.personal.bookmark_app.data.remote.dto.response.auth.AuthResponse
import com.personal.bookmark_app.data.remote.dto.response.auth.MessageResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/registration-code")
    suspend fun sendRegistrationCode(@Body request: EmailRequest): MessageResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): MessageResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
