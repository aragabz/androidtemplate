package com.aragabz.androidtemplate.feature.settings.domain.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.settings.domain.model.SettingsPreferences
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<SettingsPreferences>

    fun updateTheme(theme: ThemePreference): Flow<AppResult<Unit>>

    fun updateLanguage(language: String): Flow<AppResult<Unit>>
}
