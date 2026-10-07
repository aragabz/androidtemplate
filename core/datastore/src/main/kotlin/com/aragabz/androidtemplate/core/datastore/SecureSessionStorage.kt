package com.aragabz.androidtemplate.core.datastore

import kotlinx.coroutines.flow.Flow

/**
 * Stores the auth token encrypted at rest.
 */
interface SecureSessionStorage {
    /**
     * The stored token, emitted once it has been loaded and again on every save or clear.
     */
    val authToken: Flow<String?>

    /**
     * Synchronous read for callers that cannot suspend, such as OkHttp interceptors. Implementations serve it
     * from memory; see [SecureSessionStorageImpl] for the startup behavior.
     */
    fun getAuthToken(): String?

    suspend fun saveAuthToken(token: String)

    suspend fun clearSession()
}
