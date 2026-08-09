package com.aragabz.androidtemplate.feature.profile.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileApi
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileDto
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileRepositoryImplTest {
    private lateinit var profileApi: FakeProfileApi
    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var repository: ProfileRepositoryImpl

    @Before
    fun setup() {
        profileApi = FakeProfileApi()
        userPreferencesRepository = FakeUserPreferencesRepository()
        repository = ProfileRepositoryImpl(profileApi, userPreferencesRepository)
    }

    @Test
    fun `getProfile returns null when no user is logged in`() =
        runTest {
            // Given
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = null, authToken = null),
            )

            // When
            val result = repository.getProfile().first()

            // Then
            assertTrue(result is AppResult.Success)
            assertNull((result as AppResult.Success).data)
        }

    @Test
    fun `getProfile returns Success when user is logged in and API call succeeds`() =
        runTest {
            // Given
            val userId = "user-001"
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = userId, authToken = "token"),
            )
            val dto =
                ProfileDto(
                    userId = userId,
                    displayName = "John Doe",
                    email = "john.doe@example.com",
                )
            profileApi.profileToReturn = dto

            val expectedProfile =
                UserProfile(
                    userId = userId,
                    displayName = "John Doe",
                    email = "john.doe@example.com",
                )

            // When
            val result = repository.getProfile().first()

            // Then
            assertTrue(result is AppResult.Success)
            assertEquals(expectedProfile, (result as AppResult.Success).data)
        }

    @Test
    fun `getProfile returns Error when API call fails`() =
        runTest {
            // Given
            userPreferencesRepository.setPreferences(
                UserPreferences(userId = "user-001", authToken = "token"),
            )
            profileApi.shouldThrow = true

            // When
            val result = repository.getProfile().first()

            // Then
            assertTrue(result is AppResult.Error)
        }

    // Test fakes
    private class FakeProfileApi : ProfileApi {
        var profileToReturn: ProfileDto? = null
        var shouldThrow = false

        override suspend fun getProfile(userId: String): ProfileDto {
            if (shouldThrow) throw RuntimeException("Network error")
            return profileToReturn ?: throw IllegalStateException("No profile set")
        }
    }

    private class FakeUserPreferencesRepository : UserPreferencesRepository {
        private var preferences = UserPreferences(userId = null, authToken = null)

        fun setPreferences(prefs: UserPreferences) {
            preferences = prefs
        }

        override val userPreferences: Flow<UserPreferences>
            get() = flowOf(preferences)

        override suspend fun updateTheme(theme: AppTheme) {
            // Not used in these tests
        }

        override suspend fun saveAuthToken(token: String) {
            preferences = preferences.copy(authToken = token)
        }

        override suspend fun saveUserId(userId: String) {
            preferences = preferences.copy(userId = userId)
        }

        override suspend fun updateLanguage(language: String) {
            // Not used in these tests
        }

        override suspend fun updateBiometricAuthEnabled(enabled: Boolean) {
        }

        override suspend fun clearSession() {
            preferences = UserPreferences(userId = null, authToken = null)
        }
    }
}
