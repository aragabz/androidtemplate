package com.aragabz.androidtemplate.feature.auth.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.feature.auth.data.api.AuthApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {
    private lateinit var authApi: FakeAuthApi
    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        authApi = FakeAuthApi()
        userPreferencesRepository = FakeUserPreferencesRepository()
        repository = AuthRepositoryImpl(authApi, userPreferencesRepository)
    }

    @Test
    fun `getSession returns session from preferences`() =
        runTest {
            // Given
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = "user-001", authToken = "token-123"),
            )

            // When
            val session = repository.getSession().first()

            // Then
            assertEquals("user-001", session.userId)
            assertEquals("token-123", session.token)
        }

    @Test
    fun `getSession returns null values when not authenticated`() =
        runTest {
            // Given
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = null, authToken = null),
            )

            // When
            val session = repository.getSession().first()

            // Then
            assertNull(session.userId)
            assertNull(session.token)
        }

    @Test
    fun `signIn saves credentials to preferences`() =
        runTest {
            // Given
            val userId = "user-123"
            val token = "auth-token-456"

            // When
            val result = repository.signIn(userId, token).first { it !is AppResult.Loading }

            // Then
            assertTrue(result is AppResult.Success)
            assertEquals(userId, userPreferencesRepository.savedUserId)
            assertEquals(token, userPreferencesRepository.savedAuthToken)
        }

    @Test
    fun `signOut calls API and clears session when API succeeds`() =
        runTest {
            // Given
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = "user-001", authToken = "token"),
            )

            // When
            val result = repository.signOut().first { it !is AppResult.Loading }

            // Then
            assertTrue(authApi.signOutCalled)
            assertTrue(userPreferencesRepository.sessionCleared)
            assertTrue(result is AppResult.Success)
        }

    @Test
    fun `signOut clears local session even when API call fails`() =
        runTest {
            // Given
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = "user-001", authToken = "token"),
            )
            authApi.shouldThrow = true

            // When
            val result = repository.signOut().first { it !is AppResult.Loading }

            // Then
            assertTrue(userPreferencesRepository.sessionCleared)
            assertTrue(result is AppResult.Success)
        }

    @Test
    fun `signOut returns Error only if local clear fails`() =
        runTest {
            // Given
            userPreferencesRepository.shouldFailClear = true

            // When
            val result = repository.signOut().first { it !is AppResult.Loading }

            // Then
            assertFalse(userPreferencesRepository.sessionCleared)
            assertTrue(result is AppResult.Error)
        }

    // Test fakes
    private class FakeAuthApi : AuthApi {
        var shouldThrow = false
        var signOutCalled = false

        override suspend fun signOut() {
            signOutCalled = true
            if (shouldThrow) throw RuntimeException("Network error")
        }
    }

    private class FakeUserPreferencesRepository : UserPreferencesRepository {
        private var preferences = UserPreferences(userId = null, authToken = null)
        var savedUserId: String? = null
        var savedAuthToken: String? = null
        var sessionCleared = false
        var shouldFailClear = false

        fun setPreferences(prefs: UserPreferences) {
            preferences = prefs
        }

        override val userPreferences: Flow<UserPreferences>
            get() = flowOf(preferences)

        override suspend fun updateTheme(theme: AppTheme) {
            // Not used in these tests
        }

        override suspend fun saveAuthToken(token: String) {
            savedAuthToken = token
            preferences = preferences.copy(authToken = token)
        }

        override suspend fun saveUserId(userId: String) {
            savedUserId = userId
            preferences = preferences.copy(userId = userId)
        }

        override suspend fun updateLanguage(language: String) {
            // Not used in these tests
        }

        override suspend fun clearSession() {
            if (shouldFailClear) {
                throw RuntimeException("Failed to clear session")
            }
            sessionCleared = true
            preferences = UserPreferences(userId = null, authToken = null)
        }
    }
}
