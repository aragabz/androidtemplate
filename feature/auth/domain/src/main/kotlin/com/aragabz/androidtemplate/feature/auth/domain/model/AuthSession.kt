package com.aragabz.androidtemplate.feature.auth.domain.model

/**
 * Authentication session model used by UI and domain logic.
 *
 * Note: token is excluded from toString() to prevent accidental logging of sensitive data.
 */
data class AuthSession(
    val userId: String? = null,
    val token: String? = null,
) {
    val isAuthenticated: Boolean = !userId.isNullOrBlank() && !token.isNullOrBlank()

    /**
     * Custom toString() that excludes token to prevent accidental logging of sensitive data.
     */
    override fun toString(): String {
        return "AuthSession(userId=$userId, token=***REDACTED***, isAuthenticated=$isAuthenticated)"
    }
}
