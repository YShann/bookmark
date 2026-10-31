package com.personal.bookmark_app.ui.screen.forgotpassword


data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false
)