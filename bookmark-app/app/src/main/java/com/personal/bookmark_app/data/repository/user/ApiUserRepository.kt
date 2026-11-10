package com.personal.bookmark_app.data.repository.user

import android.content.ContentResolver
import android.net.Uri
import com.personal.bookmark_app.data.remote.api.UserApi
import com.personal.bookmark_app.data.remote.backendMessage
import com.personal.bookmark_app.data.remote.dto.request.auth.LinkGoogleRequest
import com.personal.bookmark_app.data.remote.dto.response.user.UserProfileResponse
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class ApiUserRepository(
    private val api: UserApi,
    private val contentResolver: ContentResolver
) : UserRepository {

    override suspend fun getProfile(): Result<UserProfileResponse> = runCatching {
        api.getProfile()
    }

    override suspend fun linkGoogle(
        idToken: String,
        password: String
    ): Result<UserProfileResponse> = runCatching {
        api.linkGoogle(LinkGoogleRequest(idToken, password))
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { error ->
            if (error is CancellationException) throw error
            val message = (error as? HttpException)?.backendMessage()
            Result.failure(IllegalStateException(message ?: "無法綁定 Google，請稍後再試", error))
        }
    )

    override suspend fun updateAvatar(
        imageUri: Uri
    ): Result<UserProfileResponse> = runCatching {
        val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
        val extension = when (mimeType.lowercase()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> error("僅支援 JPG、PNG 或 WebP 圖片")
        }
        val imageBytes = contentResolver.openInputStream(imageUri)
            ?.use { it.readBytes() }
            ?: error("無法讀取所選的圖片")

        require(imageBytes.isNotEmpty()) { "所選圖片是空的" }
        require(imageBytes.size <= 10 * 1024 * 1024) { "圖片大小不可超過 10 MB" }

        val imagePart = MultipartBody.Part.createFormData(
            "file",
            "avatar.$extension",
            imageBytes.toRequestBody(mimeType.toMediaType())
        )
        api.updateAvatar(imagePart)
    }
}
