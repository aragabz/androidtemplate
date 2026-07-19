package com.aragabz.androidtemplate.feature.settings.domain.model

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

data class SettingsPreferences(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val language: String = "en",
)
