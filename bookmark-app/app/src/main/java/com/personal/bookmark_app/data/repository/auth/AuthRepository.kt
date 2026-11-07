package com.personal.bookmark_app.data.repository.auth

interface AuthRepository {

    // 登入
    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    // 註冊
    suspend fun register(
        email: String,
        password: String
    ): Result<Unit>

    // 驗證信箱
    suspend fun verifyEmail(
        email: String,
        code: String
    ): Result<Unit>

    // 重新寄送驗證碼
    suspend fun resendVerificationCode(
        email: String
    ): Result<Unit>

    // 寄送忘記密碼信件
    suspend fun sendPasswordResetLink(
        email: String
    ): Result<Unit>

    suspend fun logout(): Result<Unit>
}
