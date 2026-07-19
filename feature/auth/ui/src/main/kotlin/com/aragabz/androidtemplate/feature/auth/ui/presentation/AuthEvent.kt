package com.aragabz.androidtemplate.feature.auth.ui.presentation

sealed interface AuthEvent {
    data class OnUserIdChanged(val value: String) : AuthEvent

    data object OnSignIn : AuthEvent

    data object OnSignOut : AuthEvent

    data object OnDismissError : AuthEvent
}
