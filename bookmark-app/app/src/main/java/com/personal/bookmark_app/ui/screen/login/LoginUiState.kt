package com.personal.bookmark_app.ui.screen.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
//    val rememberMe: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasSubmitted: Boolean = false
) {
    // Email 格式驗證
    val isEmailValid: Boolean
        get() = email.matches(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        )

    // Email 欄位錯誤
    val emailError: String?
        get() = when {
            !hasSubmitted -> null
            email.isBlank() -> "請輸入電子郵件"
            !isEmailValid -> "請輸入有效的電子郵件"
            else -> null
        }

    // 密碼欄位錯誤
    val passwordError: String?
        get() = when {
            !hasSubmitted -> null
            password.isBlank() -> "請輸入密碼"
            else -> null
        }

    // 表單驗證
    val isFormValid: Boolean
        get() = isEmailValid &&
                password.isNotBlank()
}