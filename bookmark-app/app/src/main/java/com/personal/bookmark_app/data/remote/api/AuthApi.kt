package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.request.auth.VerifyResetCodeRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.ResetPasswordRequest
import com.personal.bookmark_app.data.remote.dto.response.auth.VerifyResetCodeResponse

import com.personal.bookmark_app.data.remote.dto.request.auth.EmailRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.GoogleLoginRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.LoginRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.RegisterRequest
import com.personal.bookmark_app.data.remote.dto.response.auth.AuthResponse
import com.personal.bookmark_app.data.remote.dto.response.auth.MessageResponse
import com.personal.bookmark_app.data.remote.dto.request.auth.LogoutRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.RefreshTokenRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: EmailRequest): MessageResponse

    @POST("api/auth/verify-reset-code")
    suspend fun verifyResetCode(@Body request: VerifyResetCodeRequest): VerifyResetCodeResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

    @POST("api/auth/registration-code")
    suspend fun sendRegistrationCode(@Body request: EmailRequest): MessageResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): MessageResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): AuthResponse

    @POST("api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): MessageResponse

    @POST("api/auth/refresh-session")
    suspend fun refreshSession(
        @Header("X-Bookmark-Refresh-Request") requestId: String,
        @Body request: RefreshTokenRequest
    ): AuthResponse
}
