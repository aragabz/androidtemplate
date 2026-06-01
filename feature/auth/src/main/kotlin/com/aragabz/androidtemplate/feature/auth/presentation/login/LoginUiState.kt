package com.aragabz.androidtemplate.feature.auth.presentation.login

/**
 * UI state for login screen.
 * 
 * @property email User email input
 * @property password User password input
 * @property isLoading Whether login operation is in progress
 * @property errorMessage Error message to display, null if no error
 * @property isLoginSuccessful Whether login was successful
 */
public data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)
