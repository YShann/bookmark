package com.personal.bookmark_app.ui.screen.verifyemail

internal const val VERIFICATION_RESEND_SECONDS = 60

data class VerifyEmailUiState(
    val email: String = "",
    val verificationCode: String = "",
    val resendCountdown: Int = VERIFICATION_RESEND_SECONDS,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) {
    val isVerifyEnabled: Boolean
        get() = verificationCode.length == 6 && !isLoading

    val canResend: Boolean
        get() = resendCountdown <= 0
}