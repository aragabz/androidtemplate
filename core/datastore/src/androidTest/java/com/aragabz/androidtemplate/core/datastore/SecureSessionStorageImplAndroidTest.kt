package com.aragabz.androidtemplate.core.datastore

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecureSessionStorageImplAndroidTest {
    private lateinit var secureSessionStorage: SecureSessionStorage

    @Before
    fun setUp() = runBlocking {
        secureSessionStorage = SecureSessionStorageImpl(ApplicationProvider.getApplicationContext())
        secureSessionStorage.clearSession()
    }

    @Test
    fun savesAndClearsEncryptedAuthToken() = runBlocking {
        secureSessionStorage.saveAuthToken("token-456")
        assertEquals("token-456", secureSessionStorage.getAuthToken())

        secureSessionStorage.clearSession()
        assertEquals(null, secureSessionStorage.getAuthToken())
    }
}
