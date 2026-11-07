package com.personal.bookmark_app.data.repository

import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.remote.api.AuthApi
import com.personal.bookmark_app.data.remote.dto.request.auth.EmailRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.LoginRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.RegisterRequest
import com.personal.bookmark_app.data.remote.dto.response.ApiErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

class ApiAuthRepository(
    private val api: AuthApi,
    private val preferences: UserPreferencesRepository
) : AuthRepository {

    // 後端的註冊流程是「寄碼」再「驗證碼 + 密碼完成註冊」。
    // 暫存在記憶體，不落盤；離開 App 後使用者需重新填寫。
    private var pendingRegistrationPassword: String? = null

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val response = api.login(LoginRequest(email.trim(), password))
        preferences.saveTokens(response.accessToken, response.refreshToken)
    }.mapNetworkFailure()

    override suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        api.sendRegistrationCode(EmailRequest(email.trim()))
        pendingRegistrationPassword = password
    }.mapNetworkFailure()

    override suspend fun verifyEmail(email: String, code: String): Result<Unit> = runCatching {
        val password = checkNotNull(pendingRegistrationPassword) {
            "註冊資料已逾時，請重新輸入 Email 和密碼"
        }
        api.register(RegisterRequest(email.trim(), code, password))
        pendingRegistrationPassword = null
        val auth = api.login(LoginRequest(email.trim(), password))
        preferences.saveTokens(auth.accessToken, auth.refreshToken)
    }.mapNetworkFailure()

    override suspend fun resendVerificationCode(email: String): Result<Unit> =
        runCatching {
            api.sendRegistrationCode(EmailRequest(email.trim()))
            Unit
        }.mapNetworkFailure()

    override suspend fun sendPasswordResetLink(email: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("後端尚未提供忘記密碼 API"))
}

private fun <T> Result<T>.mapNetworkFailure(): Result<T> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { error ->
        val message = when (error) {
            is HttpException -> error.backendMessage() ?: when (error.code()) {
                401 -> "Email 或密碼錯誤"
                403 -> "請先完成 Email 驗證"
                409 -> "此 Email 已經註冊"
                else -> "伺服器發生錯誤 (${error.code()})"
            }
            else -> error.message ?: "連線失敗，請確認網路與後端服務"
        }
        Result.failure(IllegalStateException(message, error))
    }
)

private fun HttpException.backendMessage(): String? = runCatching {
    response()?.errorBody()?.string()
        ?.let { Gson().fromJson(it, ApiErrorResponse::class.java)?.message }
        ?.takeIf(String::isNotBlank)
}.getOrNull()
