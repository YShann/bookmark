package com.personal.bookmark_app.ui.viewmodel

sealed interface AuthEvent {

    data object Authenticated : AuthEvent

    data object LoggedOut : AuthEvent

    data object NavigateToVerifyEmail : AuthEvent

    data class ShowMessage(
        val message: String
    ) : AuthEvent
}
