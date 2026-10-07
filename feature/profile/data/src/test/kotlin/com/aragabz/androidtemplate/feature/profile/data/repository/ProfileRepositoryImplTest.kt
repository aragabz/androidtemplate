package com.aragabz.androidtemplate.feature.profile.data.repository

import app.cash.turbine.test
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.core.testing.FakeUserPreferencesRepository
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileApi
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileDto
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ProfileRepositoryImplTest {
    private val profileApi = FakeProfileApi()
    private val userPreferencesRepository = FakeUserPreferencesRepository()
    private val repository = ProfileRepositoryImpl(profileApi, userPreferencesRepository)

    @Test
    fun `emits no profile when no user is signed in`() =
        runTest {
            repository.getProfile().test {
                assertEquals(AppResult.Success(null), awaitItem())
            }
        }

    @Test
    fun `emits loading, then the fetched profile`() =
        runTest {
            userPreferencesRepository.preferences.value = UserPreferences(userId = "user-001", authToken = "token")

            repository.getProfile().test {
                assertEquals(AppResult.Loading, awaitItem())
                assertEquals(
                    AppResult.Success(
                        UserProfile(userId = "user-001", displayName = "John Doe", email = "john@example.com"),
                    ),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `emits an error when the API call fails`() =
        runTest {
            userPreferencesRepository.preferences.value = UserPreferences(userId = "user-001", authToken = "token")
            profileApi.shouldThrow = true

            repository.getProfile().test {
                assertEquals(AppResult.Loading, awaitItem())
                assertTrue(awaitItem() is AppResult.Error)
            }
        }

    @Test
    fun `refetches only when the signed-in user changes`() =
        runTest {
            userPreferencesRepository.preferences.value = UserPreferences(userId = "user-001", authToken = "token")

            repository.getProfile().test {
                skipItems(2)

                userPreferencesRepository.updateTheme(AppTheme.DARK)
                expectNoEvents()
                assertEquals(1, profileApi.calls)

                userPreferencesRepository.saveUserId("user-002")
                assertEquals(AppResult.Loading, awaitItem())
                assertEquals("user-002", (awaitItem() as AppResult.Success).data?.userId)
                assertEquals(2, profileApi.calls)
            }
        }

    private class FakeProfileApi : ProfileApi {
        var shouldThrow = false
        var calls = 0
            private set

        override suspend fun getProfile(userId: String): ProfileDto {
            calls++
            if (shouldThrow) throw IOException("Network error")
            return ProfileDto(userId = userId, displayName = "John Doe", email = "john@example.com")
        }
    }
}
