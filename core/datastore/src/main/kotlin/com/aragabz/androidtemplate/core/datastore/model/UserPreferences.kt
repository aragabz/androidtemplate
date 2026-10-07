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
) {
    /**
     * Custom toString() that excludes authToken to prevent accidental logging of sensitive data.
     */
    override fun toString(): String =
        "UserPreferences(userId=$userId, authToken=***REDACTED***, theme=$theme, language=$language)"
}

/**
 * App theme options.
 */
enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    companion object {
        /** Parses a persisted name, falling back to [SYSTEM] for missing or unknown (e.g. renamed) values. */
        fun fromStoredValue(value: String?): AppTheme = entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
