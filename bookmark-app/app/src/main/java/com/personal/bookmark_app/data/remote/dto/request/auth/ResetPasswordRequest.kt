package com.personal.bookmark_app.data.remote.dto.request.auth

class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String,
    val confirmPassword: String
)
