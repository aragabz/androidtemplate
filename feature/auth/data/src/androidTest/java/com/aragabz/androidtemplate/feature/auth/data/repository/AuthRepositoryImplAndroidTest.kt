package com.aragabz.androidtemplate.feature.auth.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.SecureSessionStorageImpl
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepositoryImpl
import com.aragabz.androidtemplate.feature.auth.data.api.AuthApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthRepositoryImplAndroidTest {
    private lateinit var preferencesRepository: UserPreferencesRepositoryImpl
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() =
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            preferencesRepository = UserPreferencesRepositoryImpl(context, SecureSessionStorageImpl(context))
            preferencesRepository.clearSession()
            val authApi =
                object : AuthApi {
                    override suspend fun signOut() = Unit
                }
            repository = AuthRepositoryImpl(authApi, preferencesRepository)
        }

    @Test
    fun signInAndSignOut_updateSessionStorage() =
        runBlocking {
            val signInResults = repository.signIn("ragab", "token-123").toList()
            assertTrue(signInResults.first() is AppResult.Loading)
            assertTrue(signInResults.last() is AppResult.Success)

            val signedInSession = repository.getSession().first()
            assertEquals("ragab", signedInSession.userId)
            assertEquals("token-123", signedInSession.token)

            val signOutResults = repository.signOut().toList()
            assertTrue(signOutResults.first() is AppResult.Loading)
            assertTrue(signOutResults.last() is AppResult.Success)
            assertEquals(null, repository.getSession().first().userId)
        }
}
