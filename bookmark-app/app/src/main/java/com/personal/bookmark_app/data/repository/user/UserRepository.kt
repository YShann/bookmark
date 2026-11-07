package com.personal.bookmark_app.data.repository.user

import android.net.Uri
import com.personal.bookmark_app.data.remote.dto.response.user.UserProfileResponse

interface UserRepository {
    suspend fun getProfile(): Result<UserProfileResponse>

    suspend fun updateAvatar(imageUri: Uri): Result<UserProfileResponse>
}
