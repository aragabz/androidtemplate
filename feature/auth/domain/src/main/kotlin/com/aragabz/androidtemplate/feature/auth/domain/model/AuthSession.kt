package com.aragabz.androidtemplate.feature.auth.domain.model

/**
 * Authentication session model used by UI and domain logic.
 */
data class AuthSession(
    val userId: String? = null,
    val token: String? = null,
) {
    val isAuthenticated: Boolean = !userId.isNullOrBlank() && !token.isNullOrBlank()
}
