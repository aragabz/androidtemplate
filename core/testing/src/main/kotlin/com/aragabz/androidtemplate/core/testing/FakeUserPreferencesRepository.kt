package com.aragabz.androidtemplate.core.testing

import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException

/**
 * In-memory [UserPreferencesRepository]. Set [preferences] directly to arrange a test; writes update it.
 */
class FakeUserPreferencesRepository(
    initial: UserPreferences = UserPreferences(),
) : UserPreferencesRepository {
    val preferences = MutableStateFlow(initial)

    /** When true, [clearSession] throws, as a failing DataStore write would. */
    var failClearSession = false

    override val userPreferences: Flow<UserPreferences> = preferences

    override suspend fun updateTheme(theme: AppTheme) {
        preferences.value = preferences.value.copy(theme = theme)
    }

    override suspend fun saveAuthToken(token: String) {
        preferences.value = preferences.value.copy(authToken = token)
    }

    override suspend fun saveUserId(userId: String) {
        preferences.value = preferences.value.copy(userId = userId)
    }

    override suspend fun updateLanguage(language: String) {
        preferences.value = preferences.value.copy(language = language)
    }

    override suspend fun clearSession() {
        if (failClearSession) throw IOException("Failed to clear session")
        preferences.value = preferences.value.copy(userId = null, authToken = null)
    }
}
