package com.aragabz.androidtemplate.feature.auth.ui.presentation

sealed interface AuthEvent {
    data class OnEmailChanged(val value: String) : AuthEvent

    data class OnPasswordChanged(val value: String) : AuthEvent

    data object OnToggleMode : AuthEvent

    data object OnSubmit : AuthEvent

    data object OnSignOut : AuthEvent

    data object OnDismissError : AuthEvent
}
