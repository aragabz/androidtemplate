package com.aragabz.androidtemplate.core.datastore.model

/**
 * User preference data class.
 */
data class UserPreferences(
    val userId: String? = null,
    val authToken: String? = null,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "en",
)

/**
 * App theme options.
 */
enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
}
