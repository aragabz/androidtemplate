package com.aragabz.androidtemplate.feature.auth.presentation.register

/**
 * UI state for register screen.
 * 
 * @property name User name input
 * @property email User email input
 * @property password User password input
 * @property confirmPassword Password confirmation input
 * @property isLoading Whether registration operation is in progress
 * @property errorMessage Error message to display, null if no error
 * @property isRegistrationSuccessful Whether registration was successful
 */
public data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)
