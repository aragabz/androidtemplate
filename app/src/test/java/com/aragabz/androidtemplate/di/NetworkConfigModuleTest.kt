package com.aragabz.androidtemplate.di

import com.aragabz.androidtemplate.core.datastore.SecureSessionStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkConfigModuleTest {
    @Test
    fun `auth token provider reads the current stored session token`() {
        val storage = FakeSecureSessionStorage()
        val provider = NetworkConfigModule.provideAuthTokenProvider(storage)

        assertNull(provider.getAuthToken())

        storage.token = "token-123"
        assertEquals("token-123", provider.getAuthToken())
    }

    private class FakeSecureSessionStorage : SecureSessionStorage {
        var token: String? = null

        override val authToken: Flow<String?> get() = flowOf(token)

        override fun getAuthToken(): String? = token

        override suspend fun saveAuthToken(token: String) {
            this.token = token
        }

        override suspend fun clearSession() {
            token = null
        }
    }
}
