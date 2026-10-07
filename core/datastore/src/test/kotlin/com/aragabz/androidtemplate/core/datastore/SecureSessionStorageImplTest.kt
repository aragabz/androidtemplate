package com.aragabz.androidtemplate.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SecureSessionStorageImplTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val keyProvider = FakeSecretKeyProvider()
    private val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(scope = scope) { tempFolder.newFile("secure_session.preferences_pb") }
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    private fun storage(beforeLoad: () -> Unit = {}) =
        SecureSessionStorageImpl(dataStore, TokenCipher(keyProvider), scope, Dispatchers.IO, beforeLoad)

    private suspend fun storedValue(): String? = dataStore.data.first()[KEY_AUTH_TOKEN]

    @Test
    fun `saved token is cached, encrypted at rest and readable after a restart`() =
        runTest {
            storage().saveAuthToken("token-123")

            val stored = storedValue()
            assertNotEquals("token-123", stored)
            assertEquals("token-123", TokenCipher(keyProvider).decrypt(checkNotNull(stored)))

            val restarted = storage()
            assertEquals("token-123", restarted.getAuthToken())
            assertEquals("token-123", restarted.authToken.first())
        }

    @Test
    fun `authToken emits saves and clears`() =
        runTest {
            val storage = storage()

            storage.authToken.test {
                assertNull(awaitItem())

                storage.saveAuthToken("token-1")
                assertEquals("token-1", awaitItem())

                storage.clearSession()
                assertNull(awaitItem())
            }
            assertNull(storage.getAuthToken())
            assertNull(storedValue())
        }

    @Test
    fun `undecryptable token is cleared together with the key`() =
        runTest {
            storage().saveAuthToken("token-123")
            // E.g. the Keystore entry was wiped after a backup restore: a new key can't decrypt the stored token.
            keyProvider.deleteKey()
            val deletionsBefore = keyProvider.deletedKeys

            val restarted = storage()

            assertNull(restarted.getAuthToken())
            assertNull(storedValue())
            assertEquals(deletionsBefore + 1, keyProvider.deletedKeys)
        }

    @Test
    fun `corrupted stored value is cleared`() =
        runTest {
            dataStore.edit { it[KEY_AUTH_TOKEN] = "not a payload" }

            assertNull(storage().authToken.first())
            assertNull(storedValue())
        }

    @Test
    fun `save replaces an invalidated key and retries once`() =
        runTest {
            val storage = storage()
            storage.authToken.first()
            keyProvider.failNextGet = true

            storage.saveAuthToken("token-123")

            assertEquals(1, keyProvider.deletedKeys)
            assertEquals("token-123", storage.getAuthToken())
            assertEquals("token-123", storage().getAuthToken())
        }

    @Test
    fun `legacy cleanup runs before the first load`() =
        runTest {
            var cleanedUp = false

            storage(beforeLoad = { cleanedUp = true }).authToken.first()

            assertEquals(true, cleanedUp)
        }

    private companion object {
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }
}
