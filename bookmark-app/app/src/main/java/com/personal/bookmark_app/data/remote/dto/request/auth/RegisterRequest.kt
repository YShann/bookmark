package com.personal.bookmark_app.data.remote.dto.request.auth

data class RegisterRequest(
    val email: String,
    val verificationCode: String,
    val password: String
)
