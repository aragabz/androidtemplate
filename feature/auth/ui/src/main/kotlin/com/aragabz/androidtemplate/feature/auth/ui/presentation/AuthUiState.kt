package com.aragabz.androidtemplate.feature.auth.ui.presentation

import com.aragabz.androidtemplate.core.common.ui.UiText

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
    val currentUserEmail: String? = null,
    val error: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val biometricAuthEnabled: Boolean = false,
    val biometricAuthAvailable: Boolean = false,
    val showBiometricPrompt: Boolean = false,
) {
    /**
     * Custom toString() that excludes password to prevent accidental logging of sensitive data.
     */
    override fun toString(): String {
        return "AuthUiState(email=$email, ****** isSignUpMode=$isSignUpMode, " +
                "isAuthenticated=$isAuthenticated, isLoading=$isLoading, currentUserEmail=$currentUserEmail, " +
                "error=$error, emailError=$emailError, passwordError=$passwordError, " +
                "biometricAuthEnabled=$biometricAuthEnabled, biometricAuthAvailable=$biometricAuthAvailable)"
    }
}
