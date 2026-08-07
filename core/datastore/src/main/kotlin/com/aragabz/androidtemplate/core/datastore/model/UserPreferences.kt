package com.aragabz.androidtemplate.core.datastore.model

/**
 * User preference data class.
 *
 * Note: authToken is excluded from toString() to prevent accidental logging of sensitive data.
 */
data class UserPreferences(
    val userId: String? = null,
    val authToken: String? = null,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "en",
    val biometricAuthEnabled: Boolean = false,
) {
    /**
     * Custom toString() that excludes authToken to prevent accidental logging of sensitive data.
     */
    override fun toString(): String =
        "UserPreferences(userId=$userId, authToken=***REDACTED***, theme=$theme, language=$language, biometricAuthEnabled=$biometricAuthEnabled)"
}

/**
 * App theme options.
 */
enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
}
