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
import kotlinx.coroutines.flow.map
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
    ) : UserPreferencesRepository {
        private object PreferencesKeys {
            val USER_ID = stringPreferencesKey("user_id")
            val AUTH_TOKEN = stringPreferencesKey("auth_token")
            val THEME = stringPreferencesKey("theme")
            val LANGUAGE = stringPreferencesKey("language")
        }

        override val userPreferences: Flow<UserPreferences> =
            context.dataStore.data.map { preferences ->
                UserPreferences(
                    userId = preferences[PreferencesKeys.USER_ID],
                    authToken = preferences[PreferencesKeys.AUTH_TOKEN],
                    theme =
                        preferences[PreferencesKeys.THEME]?.let {
                            AppTheme.valueOf(it)
                        } ?: AppTheme.SYSTEM,
                    language = preferences[PreferencesKeys.LANGUAGE] ?: "en",
                )
            }

        override suspend fun updateTheme(theme: AppTheme) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.THEME] = theme.name
            }
        }

        override suspend fun saveAuthToken(token: String) {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.AUTH_TOKEN] = token
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
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.USER_ID)
                preferences.remove(PreferencesKeys.AUTH_TOKEN)
            }
        }
    }
