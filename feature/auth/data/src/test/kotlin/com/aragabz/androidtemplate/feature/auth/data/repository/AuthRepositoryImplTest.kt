package com.aragabz.androidtemplate.feature.auth.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.core.testing.FakeUserPreferencesRepository
import com.aragabz.androidtemplate.feature.auth.data.api.AuthApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

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
            userPreferencesRepository.preferences.value =
                UserPreferences(userId = "user-001", authToken = "token-123")

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
            userPreferencesRepository.preferences.value =
                UserPreferences(userId = null, authToken = null)

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
            assertEquals(userId, userPreferencesRepository.preferences.value.userId)
            assertEquals(token, userPreferencesRepository.preferences.value.authToken)
        }

    @Test
    fun `signOut calls API and clears session when API succeeds`() =
        runTest {
            // Given
            userPreferencesRepository.preferences.value =
                UserPreferences(userId = "user-001", authToken = "token")

            // When
            val result = repository.signOut().first { it !is AppResult.Loading }

            // Then
            assertTrue(authApi.signOutCalled)
            assertNull(userPreferencesRepository.preferences.value.userId)
            assertTrue(result is AppResult.Success)
        }

    @Test
    fun `signOut clears local session even when API call fails`() =
        runTest {
            // Given
            userPreferencesRepository.preferences.value =
                UserPreferences(userId = "user-001", authToken = "token")
            authApi.shouldThrow = true

            // When
            val result = repository.signOut().first { it !is AppResult.Loading }

            // Then
            assertNull(userPreferencesRepository.preferences.value.userId)
            assertTrue(result is AppResult.Success)
        }

    @Test
    fun `signOut returns Error only if local clear fails`() =
        runTest {
            // Given
            userPreferencesRepository.preferences.value = UserPreferences(userId = "user-001", authToken = "token")
            userPreferencesRepository.failClearSession = true

            // When
            val result = repository.signOut().first { it !is AppResult.Loading }

            // Then
            assertEquals("user-001", userPreferencesRepository.preferences.value.userId)
            assertTrue(result is AppResult.Error)
        }

    @Test
    fun `signIn propagates cancellation instead of emitting Error`() =
        runTest {
            val cancelling =
                object : UserPreferencesRepository by userPreferencesRepository {
                    override suspend fun saveUserId(userId: String): Unit = throw CancellationException("cancelled")
                }
            val emitted = mutableListOf<AppResult<Unit>>()

            val thrown = runCatching { AuthRepositoryImpl(authApi, cancelling).signIn("u", "t").toList(emitted) }

            assertTrue(thrown.exceptionOrNull() is CancellationException)
            assertTrue(emitted.none { it is AppResult.Error })
        }

    @Test
    fun `signOut propagates cancellation of the server call instead of clearing the session`() =
        runTest {
            userPreferencesRepository.preferences.value = UserPreferences(userId = "user-001", authToken = "token")
            authApi.throwable = CancellationException("cancelled")

            val thrown = runCatching { repository.signOut().toList() }

            assertTrue(thrown.exceptionOrNull() is CancellationException)
            assertEquals("user-001", userPreferencesRepository.preferences.value.userId)
        }

    // Test fakes
    private class FakeAuthApi : AuthApi {
        var shouldThrow = false
        var throwable: Throwable? = null
        var signOutCalled = false

        override suspend fun signOut() {
            signOutCalled = true
            throwable?.let { throw it }
            if (shouldThrow) throw IOException("Network error")
        }
    }
}
