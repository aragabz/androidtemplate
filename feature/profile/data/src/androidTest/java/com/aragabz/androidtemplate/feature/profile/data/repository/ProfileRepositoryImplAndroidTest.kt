package com.aragabz.androidtemplate.feature.profile.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.SecureSessionStorageImpl
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepositoryImpl
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileApi
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileRepositoryImplAndroidTest {
    private lateinit var preferencesRepository: UserPreferencesRepositoryImpl
    private lateinit var repository: ProfileRepositoryImpl

    @Before
    fun setUp() =
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            preferencesRepository = UserPreferencesRepositoryImpl(context, SecureSessionStorageImpl(context))
            preferencesRepository.clearSession()
            val profileApi =
                object : ProfileApi {
                    override suspend fun getProfile(userId: String) =
                        ProfileDto(userId = userId, displayName = "Ragab", email = "ragab@example.com")
                }
            repository = ProfileRepositoryImpl(profileApi, preferencesRepository)
        }

    @Test
    fun getProfile_mapsStoredSessionIntoProfile() =
        runBlocking {
            preferencesRepository.saveUserId("ragab")

            val result = repository.getProfile().first()

            val profile = (result as AppResult.Success).data
            requireNotNull(profile)
            assertEquals("ragab", profile.userId)
            assertEquals("Ragab", profile.displayName)
            assertEquals("ragab@example.com", profile.email)
        }
}
