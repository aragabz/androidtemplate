package com.aragabz.androidtemplate.core.datastore

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserPreferencesRepositoryImplAndroidTest {
    private lateinit var repository: UserPreferencesRepositoryImpl
    private lateinit var secureSessionStorage: SecureSessionStorage

    @Before
    fun setUp() = runBlocking {
        secureSessionStorage = SecureSessionStorageImpl(ApplicationProvider.getApplicationContext())
        repository =
            UserPreferencesRepositoryImpl(
                context = ApplicationProvider.getApplicationContext(),
                secureSessionStorage = secureSessionStorage,
            )
        repository.clearSession()
        repository.updateTheme(AppTheme.SYSTEM)
        repository.updateLanguage("en")
    }

    @Test
    fun persistsUpdatedPreferences() = runBlocking {
        repository.saveUserId("ragab")
        repository.saveAuthToken("token-123")
        repository.updateTheme(AppTheme.DARK)
        repository.updateLanguage("ar")

        val preferences = repository.userPreferences.first()

        assertEquals("ragab", preferences.userId)
        assertEquals("token-123", preferences.authToken)
        assertEquals(AppTheme.DARK, preferences.theme)
        assertEquals("ar", preferences.language)
        assertEquals("token-123", secureSessionStorage.getAuthToken())
    }
}
