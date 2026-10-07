package com.aragabz.androidtemplate.feature.auth.ui.presentation

import com.aragabz.androidtemplate.core.ui.text.UiText

/**
 * Authentication UI state.
 *
 * Note: password is excluded from toString() to prevent accidental logging of sensitive data.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isSignUpMode: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
) {
    /**
     * Custom toString() that excludes password to prevent accidental logging of sensitive data.
     */
    override fun toString(): String =
        "AuthUiState(email=$email, password=***REDACTED***, isSignUpMode=$isSignUpMode, " +
            "isAuthenticated=$isAuthenticated, isLoading=$isLoading, " +
            "error=$error, emailError=$emailError, passwordError=$passwordError)"
}
