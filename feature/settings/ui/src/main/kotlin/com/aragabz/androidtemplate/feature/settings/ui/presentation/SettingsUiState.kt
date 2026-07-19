package com.aragabz.androidtemplate.feature.settings.ui.presentation

import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference

data class SettingsUiState(
    val selectedTheme: ThemePreference = ThemePreference.SYSTEM,
    val selectedLanguage: String = "en",
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
