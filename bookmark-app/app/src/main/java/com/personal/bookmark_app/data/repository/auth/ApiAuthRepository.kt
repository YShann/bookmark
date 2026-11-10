package com.personal.bookmark_app.data.repository.auth
import com.personal.bookmark_app.data.remote.dto.request.auth.VerifyResetCodeRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.ResetPasswordRequest

import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.remote.api.AuthApi
import com.personal.bookmark_app.data.remote.dto.request.auth.EmailRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.GoogleLoginRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.LoginRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.LogoutRequest
import com.personal.bookmark_app.data.remote.dto.request.auth.RegisterRequest
import com.personal.bookmark_app.data.remote.backendMessage
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

    override suspend fun loginWithGoogle(idToken: String, password: String?): Result<Unit> = runCatching {
        val response = try {
            api.loginWithGoogle(GoogleLoginRequest(idToken, password))
        } catch (error: HttpException) {
            if (error.code() == 409) {
                val action = error.response()?.headers()?.get("X-Bookmark-Auth-Action")
                val linkRequired = action == "link-google" ||
                    error.backendMessage() == "此 Email 已有帳號，請輸入原本的 App 密碼來綁定 Google"
                if (password == null && linkRequired) throw GoogleLinkRequiredException()

                val message = if (linkRequired) {
                    "後端未收到綁定所需的密碼，請重新開啟 Google 登入再試"
                } else {
                    "目前連線的後端尚未支援登入時綁定，請重啟新版 Spring Boot 後再試"
                }
                throw IllegalStateException(message, error)
            }
            throw error
        }
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

    override suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        api.forgotPassword(EmailRequest(email.trim()))
        Unit
    }.mapNetworkFailure()

    override suspend fun verifyResetCode(email: String, code: String): Result<String> = runCatching {
        val response = api.verifyResetCode(
            VerifyResetCodeRequest(email.trim(), code)
        )
        check(!response.resetToken.isNullOrBlank()) { "驗證回應不完整，請重新驗證" }
        response.resetToken
    }.mapNetworkFailure()

    override suspend fun resetPassword(resetToken: String, newPassword: String, confirmPassword: String): Result<Unit> = runCatching {
        api.resetPassword(ResetPasswordRequest(
            resetToken, newPassword, confirmPassword
        ))
        preferences.clearTokens()
        Unit
    }.mapNetworkFailure()

    override suspend fun logout(): Result<Unit> {
        val result = runCatching {
            preferences.refreshToken()?.let { api.logout(LogoutRequest(it)) }
        }.mapNetworkFailure()
        preferences.clearTokens()
        return result.map { Unit }
    }
}

private fun <T> Result<T>.mapNetworkFailure(): Result<T> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { error ->
        if (error is kotlinx.coroutines.CancellationException) throw error
        if (error is GoogleLinkRequiredException) return@fold Result.failure(error)
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
