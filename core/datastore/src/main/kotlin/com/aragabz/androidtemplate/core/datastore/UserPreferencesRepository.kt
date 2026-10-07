package com.aragabz.androidtemplate.core.datastore

import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user preferences.
 */
interface UserPreferencesRepository {
    /**
     * Flow of user preferences.
     */
    val userPreferences: Flow<UserPreferences>

    /**
     * Update the app theme.
     */
    suspend fun updateTheme(theme: AppTheme)

    /**
     * Save authentication token.
     */
    suspend fun saveAuthToken(token: String)

    /**
     * Save user ID.
     */
    suspend fun saveUserId(userId: String)

    /**
     * Update language preference.
     */
    suspend fun updateLanguage(language: String)

    /**
     * Clear all session data (logout).
     */
    suspend fun clearSession()
}
