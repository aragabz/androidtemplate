package com.aragabz.androidtemplate.core.datastore

import java.security.InvalidKeyException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Software AES key in place of the Android Keystore, which the JVM (and Robolectric) don't provide.
 */
internal class FakeSecretKeyProvider : SecretKeyProvider {
    private var key: SecretKey? = null
    var deletedKeys = 0
        private set

    /** Makes the next [getOrCreateKey] call fail like a permanently invalidated Keystore key. */
    var failNextGet = false

    override fun getOrCreateKey(): SecretKey {
        if (failNextGet) {
            failNextGet = false
            throw InvalidKeyException("Key permanently invalidated")
        }
        return key ?: KeyGenerator
            .getInstance("AES")
            .apply { init(KEY_SIZE_BITS) }
            .generateKey()
            .also { key = it }
    }

    override fun deleteKey() {
        deletedKeys++
        key = null
    }

    private companion object {
        const val KEY_SIZE_BITS = 256
    }
}
