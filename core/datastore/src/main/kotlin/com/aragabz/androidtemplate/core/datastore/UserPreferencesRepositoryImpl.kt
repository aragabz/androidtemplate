package com.aragabz.androidtemplate.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Implementation of UserPreferencesRepository using DataStore.
 */
@Singleton
class UserPreferencesRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val secureSessionStorage: SecureSessionStorage,
    ) : UserPreferencesRepository {
        private object PreferencesKeys {
            val USER_ID = stringPreferencesKey("user_id")
            val AUTH_TOKEN = stringPreferencesKey("auth_token")
            val THEME = stringPreferencesKey("theme")
            val LANGUAGE = stringPreferencesKey("language")
            val BIOMETRIC_AUTH_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey(
                "biometric_auth_enabled",
            )
        }

        private val secureAuthToken = MutableStateFlow(secureSessionStorage.getAuthToken())

        override val userPreferences: Flow<UserPreferences> =
            context.dataStore.data.combine(secureAuthToken) { preferences, authToken ->
                UserPreferences(
                    userId = preferences[PreferencesKeys.USER_ID],
                    authToken = authToken ?: preferences[PreferencesKeys.AUTH_TOKEN],
                    theme =
                        preferences[PreferencesKeys.THEME]?.let {
                            AppTheme.valueOf(it)
                        } ?: AppTheme.SYSTEM,
                    language = preferences[PreferencesKeys.LANGUAGE] ?: "en",
                    biometricAuthEnabled = preferences[PreferencesKeys.BIOMETRIC_AUTH_ENABLED] ?: false,
                )
            }

        override suspend fun updateTheme(theme: AppTheme) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.THEME] = theme.name
            }
        }

        override suspend fun saveAuthToken(token: String) {
            secureSessionStorage.saveAuthToken(token)
            secureAuthToken.value = token
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.AUTH_TOKEN)
            }
        }

        override suspend fun saveUserId(userId: String) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.USER_ID] = userId
            }
        }

        override suspend fun updateLanguage(language: String) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.LANGUAGE] = language
            }
        }

        override suspend fun updateBiometricAuthEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.BIOMETRIC_AUTH_ENABLED] = enabled
            }
        }

        override suspend fun clearSession() {
            secureSessionStorage.clearSession()
            secureAuthToken.value = null
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.USER_ID)
                preferences.remove(PreferencesKeys.AUTH_TOKEN)
            }
        }
    }
