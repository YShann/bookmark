package com.personal.bookmark_app.data.repository.user

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ApiUserRepository @Inject constructor(
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

        val (uploadExtension, uploadType, uploadBytes) = if (extension == "webp") {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            require(options.outWidth > 0 && options.outHeight > 0) { "無法讀取所選的圖片" }
            require(options.outWidth.toLong() * options.outHeight <= 16_000_000) {
                "頭像不可超過 1600 萬像素"
            }
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: error("無法讀取所選的圖片")
            val png = try {
                ByteArrayOutputStream().use {
                    check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) { "無法轉換頭像圖片" }
                    it.toByteArray()
                }
            } finally {
                bitmap.recycle()
            }
            require(png.size <= 10 * 1024 * 1024) { "處理後的圖片不可超過 10 MB" }
            Triple("png", "image/png", png)
        } else {
            Triple(extension, mimeType, imageBytes)
        }
        val imagePart = MultipartBody.Part.createFormData(
            "file",
            "avatar.$uploadExtension",
            uploadBytes.toRequestBody(uploadType.toMediaType())
        )
        api.updateAvatar(imagePart)
    }
}
