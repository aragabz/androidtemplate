package com.aragabz.androidtemplate.feature.settings.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.feature.settings.domain.model.SettingsPreferences
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import com.aragabz.androidtemplate.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : SettingsRepository {
        override fun observeSettings(): Flow<SettingsPreferences> =
            userPreferencesRepository.userPreferences.map { prefs ->
                SettingsPreferences(
                    theme =
                        when (prefs.theme) {
                            AppTheme.SYSTEM -> ThemePreference.SYSTEM
                            AppTheme.LIGHT -> ThemePreference.LIGHT
                            AppTheme.DARK -> ThemePreference.DARK
                        },
                    language = prefs.language,
                )
            }

        override fun updateTheme(theme: ThemePreference): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                runCatching {
                    userPreferencesRepository.updateTheme(
                        when (theme) {
                            ThemePreference.SYSTEM -> AppTheme.SYSTEM
                            ThemePreference.LIGHT -> AppTheme.LIGHT
                            ThemePreference.DARK -> AppTheme.DARK
                        },
                    )
                }.onSuccess {
                    emit(AppResult.Success(Unit))
                }.onFailure {
                    emit(AppResult.Error(it))
                }
            }

        override fun updateLanguage(language: String): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                runCatching {
                    userPreferencesRepository.updateLanguage(language)
                }.onSuccess {
                    emit(AppResult.Success(Unit))
                }.onFailure {
                    emit(AppResult.Error(it))
                }
            }
    }
