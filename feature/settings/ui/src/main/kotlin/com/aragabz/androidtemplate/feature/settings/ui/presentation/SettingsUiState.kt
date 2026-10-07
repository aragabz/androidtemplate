package com.aragabz.androidtemplate.feature.settings.ui.presentation

import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.feature.settings.domain.model.AppLanguage
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference

data class SettingsUiState(
    val selectedTheme: ThemePreference = ThemePreference.SYSTEM,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
