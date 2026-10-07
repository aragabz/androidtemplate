package com.aragabz.androidtemplate.feature.settings.domain.model

/**
 * Languages the user can pick: only those the app ships translations for (`values-<code>` resources).
 * To add one, translate the string resources first, then add an entry here.
 *
 * @property code ISO 639-1 language code
 * @property displayName the language's name in itself, so it reads the same in every locale
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    ;

    companion object {
        /**
         * Find language by code, defaulting to English if not found.
         */
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
    }
}
