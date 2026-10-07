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

            // Builds before SecureSessionStorage kept the token here in plaintext. It is no longer read (those
            // installs sign in again, like the EncryptedSharedPreferences ones); signing in or out deletes it.
            val LEGACY_AUTH_TOKEN = stringPreferencesKey("auth_token")
            val THEME = stringPreferencesKey("theme")
            val LANGUAGE = stringPreferencesKey("language")
        }

        override val userPreferences: Flow<UserPreferences> =
            context.dataStore.data.combine(secureSessionStorage.authToken) { preferences, authToken ->
                UserPreferences(
                    userId = preferences[PreferencesKeys.USER_ID],
                    authToken = authToken,
                    theme = AppTheme.fromStoredValue(preferences[PreferencesKeys.THEME]),
                    language = preferences[PreferencesKeys.LANGUAGE] ?: "en",
                )
            }

        override suspend fun updateTheme(theme: AppTheme) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.THEME] = theme.name
            }
        }

        override suspend fun saveAuthToken(token: String) {
            secureSessionStorage.saveAuthToken(token)
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.LEGACY_AUTH_TOKEN)
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

        override suspend fun clearSession() {
            secureSessionStorage.clearSession()
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.USER_ID)
                preferences.remove(PreferencesKeys.LEGACY_AUTH_TOKEN)
            }
        }
    }
