package com.aragabz.androidtemplate.feature.settings.ui.presentation

import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference

sealed interface SettingsEvent {
    data class OnThemeSelected(val theme: ThemePreference) : SettingsEvent

    data class OnLanguageSelected(val language: String) : SettingsEvent

    data object OnDismissError : SettingsEvent
}
