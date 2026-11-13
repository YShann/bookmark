package com.personal.bookmark_app.data.remote.dto.request.auth

data class GoogleLoginRequest(
    val idToken: String,
    val password: String?
)
