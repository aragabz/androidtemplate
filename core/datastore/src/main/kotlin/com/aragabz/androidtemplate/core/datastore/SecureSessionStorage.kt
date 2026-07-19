package com.aragabz.androidtemplate.core.datastore

interface SecureSessionStorage {
    fun getAuthToken(): String?

    suspend fun saveAuthToken(token: String)

    suspend fun clearSession()
}
