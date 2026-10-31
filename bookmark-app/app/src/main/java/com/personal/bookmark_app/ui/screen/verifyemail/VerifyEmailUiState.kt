package com.personal.bookmark_app.ui.screen.verifyemail

data class VerifyEmailUiState(
    val email: String = "",
    val verificationCode: String = "",
    val resendCountdown: Int = 45,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) {
    val isVerifyEnabled: Boolean
        get() = verificationCode.length == 6 && !isLoading

    val canResend: Boolean
        get() = resendCountdown <= 0
}