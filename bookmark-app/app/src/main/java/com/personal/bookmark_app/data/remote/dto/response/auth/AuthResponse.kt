package com.personal.bookmark_app.data.remote.dto.response.auth

data class AuthResponse(
    val userId: Long,
    val email: String,
    val accessToken: String,
    val refreshToken: String
)
