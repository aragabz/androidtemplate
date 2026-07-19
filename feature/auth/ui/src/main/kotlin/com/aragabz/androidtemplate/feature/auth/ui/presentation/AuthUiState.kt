package com.aragabz.androidtemplate.feature.auth.ui.presentation

import com.aragabz.androidtemplate.core.common.ui.UiText

data class AuthUiState(
    val userIdInput: String = "",
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val currentUserId: String? = null,
    val error: UiText? = null,
)
