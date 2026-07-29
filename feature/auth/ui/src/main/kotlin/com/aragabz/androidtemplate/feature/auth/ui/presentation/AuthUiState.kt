package com.aragabz.androidtemplate.feature.auth.ui.presentation

import com.aragabz.androidtemplate.core.common.ui.UiText

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
)
