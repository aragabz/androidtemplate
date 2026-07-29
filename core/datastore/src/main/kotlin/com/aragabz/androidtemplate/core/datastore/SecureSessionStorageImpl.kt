package com.aragabz.androidtemplate.core.datastore

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureSessionStorageImpl(
    context: Context,
) : SecureSessionStorage {
    private val sharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            MasterKey
                .Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    override fun getAuthToken(): String? = sharedPreferences.getString(KEY_AUTH_TOKEN, null)

    override suspend fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    override suspend fun clearSession() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    private companion object {
        const val FILE_NAME = "secure_session_storage"
        const val KEY_AUTH_TOKEN = "auth_token"
    }
}
