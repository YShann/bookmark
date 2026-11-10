package com.personal.bookmark_app.data.remote.dto.response.user

data class UserProfileResponse(
    val email: String,
    val avatarImageUrl: String?,
    val googleLinked: Boolean = false
)
