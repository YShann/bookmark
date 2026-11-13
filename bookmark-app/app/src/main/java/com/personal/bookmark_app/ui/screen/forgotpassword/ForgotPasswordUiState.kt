package com.personal.bookmark_app.ui.screen.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val step: PasswordResetStep = PasswordResetStep.EMAIL,
    val code: String = "",
    val resendCountdown: Int = 0,
    val isLoading: Boolean = false,
    val hasSubmitted: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmailValid: Boolean
        get() = email.trim().matches(
            Regex("^[A-Za-z0-9+_%.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        )

    val emailError: String?
        get() = when {
            !hasSubmitted -> null
            email.isBlank() -> "請輸入電子郵件"
            !isEmailValid -> "請輸入有效的電子郵件"
            else -> null
        }

    val isFormValid: Boolean
        get() = isEmailValid
}

enum class PasswordResetStep { EMAIL, CODE, PASSWORD, SUCCESS }
