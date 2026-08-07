package com.aragabz.androidtemplate.feature.settings.domain.model

/**
 * Supported app languages.
 *
 * @property code ISO 639-1 language code
 * @property displayName User-friendly display name
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
) {
    ENGLISH("en", "English"),
    ARABIC("ar", "العربية"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    ITALIAN("it", "Italiano"),
    PORTUGUESE("pt", "Português"),
    RUSSIAN("ru", "Русский"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    ;

    companion object {
        /**
         * Find language by code, defaulting to English if not found.
         */
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH

        /**
         * Get all supported language codes.
         */
        fun supportedCodes(): List<String> = entries.map { it.code }
    }
}
