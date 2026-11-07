package com.personal.bookmark_app.ui.viewmodel

sealed interface AuthEvent {

    data object Authenticated : AuthEvent

    data object NavigateToVerifyEmail : AuthEvent

    data object PasswordResetLinkSent : AuthEvent

    data class ShowMessage(
        val message: String
    ) : AuthEvent
}