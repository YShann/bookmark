package com.personal.bookmark_app.data.remote.dto.request.auth

data class VerifyResetCodeRequest(
    val email: String,
    val code: String
)
