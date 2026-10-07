package com.aragabz.androidtemplate.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.ProviderException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private val Context.secureSessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "secure_session",
    // A corrupted file only loses the session: the user signs in again.
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

/**
 * Keeps the auth token encrypted with an Android Keystore AES/GCM key ([AndroidKeystoreKeyProvider]) in the
 * `secure_session` Preferences DataStore, as Base64 `iv | ciphertext` ([TokenCipher]).
 *
 * Threading: the stored token is decrypted once, on [ioDispatcher], right after construction and then kept in
 * memory. [getAuthToken] waits for that first load (at most [LOAD_TIMEOUT_SECONDS]) and otherwise only reads the
 * cache, so the OkHttp interceptor never decrypts or touches disk; don't call it on the main thread during startup.
 * [authToken] emits once the load is done. Saves and clears write through and update the cache.
 *
 * Failures: if the token can't be decrypted (key permanently invalidated or deleted, e.g. after a backup restore,
 * or corrupted data) the stored token and the key are deleted and the user is signed out instead of crashing.
 *
 * Migration: builds before this used EncryptedSharedPreferences (`secure_session_storage`). That file is deleted
 * on first run without reading it, so those installs sign in again.
 */
class SecureSessionStorageImpl internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val cipher: TokenCipher,
    scope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    beforeLoad: () -> Unit = {},
) : SecureSessionStorage {
    constructor(
        context: Context,
        scope: CoroutineScope = CoroutineScope(SupervisorJob()),
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : this(
        dataStore = context.applicationContext.secureSessionDataStore,
        cipher = TokenCipher(AndroidKeystoreKeyProvider()),
        scope = scope,
        ioDispatcher = ioDispatcher,
        beforeLoad = { context.applicationContext.deleteSharedPreferences(LEGACY_PREFS_FILE_NAME) },
    )

    private val mutex = Mutex()
    private val cachedToken = MutableStateFlow<String?>(null)
    private val loaded = CountDownLatch(1)
    private val initialLoad: Job =
        scope.launch(ioDispatcher) {
            try {
                beforeLoad()
                cachedToken.value = mutex.withLock { readStoredToken() }
            } finally {
                loaded.countDown()
            }
        }

    override val authToken: Flow<String?> =
        flow {
            initialLoad.join()
            emitAll(cachedToken)
        }

    override fun getAuthToken(): String? {
        loaded.await(LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        return cachedToken.value
    }

    override suspend fun saveAuthToken(token: String) {
        initialLoad.join()
        withContext(ioDispatcher) {
            mutex.withLock {
                val encrypted = encryptOrNull(token) ?: run {
                    // The key may have been invalidated: start over with a new one, once.
                    deleteKeyQuietly()
                    cipher.encrypt(token)
                }
                dataStore.edit { it[KEY_AUTH_TOKEN] = encrypted }
                cachedToken.value = token
            }
        }
    }

    override suspend fun clearSession() {
        initialLoad.join()
        withContext(ioDispatcher) {
            mutex.withLock {
                dataStore.edit { it.remove(KEY_AUTH_TOKEN) }
                cachedToken.value = null
            }
        }
    }

    private suspend fun readStoredToken(): String? =
        try {
            dataStore.data.first()[KEY_AUTH_TOKEN]?.let { stored ->
                decryptOrNull(stored).also { token ->
                    if (token == null) {
                        deleteKeyQuietly()
                        dataStore.edit { it.remove(KEY_AUTH_TOKEN) }
                    }
                }
            }
        } catch (ignored: IOException) {
            // Unreadable storage: start signed out rather than crash.
            null
        }

    private fun encryptOrNull(token: String): String? = cryptoOrNull { cipher.encrypt(token) }

    private fun decryptOrNull(stored: String): String? = cryptoOrNull { cipher.decrypt(stored) }

    private fun deleteKeyQuietly() {
        cryptoOrNull { cipher.deleteKey() }
    }

    private inline fun <T> cryptoOrNull(block: () -> T): T? =
        try {
            block()
        } catch (ignored: GeneralSecurityException) {
            null
        } catch (ignored: ProviderException) {
            // Keystore failures surface as ProviderException on some devices.
            null
        } catch (ignored: IllegalArgumentException) {
            // Not a payload written by TokenCipher (bad Base64 or truncated).
            null
        }

    private companion object {
        const val LEGACY_PREFS_FILE_NAME = "secure_session_storage"
        const val LOAD_TIMEOUT_SECONDS = 5L
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }
}
