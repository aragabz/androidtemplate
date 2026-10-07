package com.aragabz.androidtemplate.core.datastore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Supplies the AES key used by [TokenCipher]. Production uses [AndroidKeystoreKeyProvider]; tests use a software key
 * (Robolectric has no AndroidKeyStore).
 */
internal interface SecretKeyProvider {
    fun getOrCreateKey(): SecretKey

    fun deleteKey()
}

/**
 * A 256-bit AES/GCM key kept in the Android Keystore under [alias]. It never leaves secure hardware (where
 * available) and needs no user authentication, so the token can be read in the background.
 */
internal class AndroidKeystoreKeyProvider(
    private val alias: String = DEFAULT_ALIAS,
) : SecretKeyProvider {
    override fun getOrCreateKey(): SecretKey {
        (keyStore().getKey(alias, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE_BITS)
                .build(),
        )
        return generator.generateKey()
    }

    override fun deleteKey() {
        keyStore().deleteEntry(alias)
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val DEFAULT_ALIAS = "session_key"
        const val KEY_SIZE_BITS = 256
    }
}

/**
 * Encrypts strings with AES/GCM and encodes the result as Base64 of `ivLength (1 byte) | iv | ciphertext+tag`.
 * A fresh IV is generated for every encryption (the Keystore requires randomized encryption).
 */
internal class TokenCipher(
    private val keyProvider: SecretKeyProvider,
) {
    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keyProvider.getOrCreateKey())
        return encodePayload(cipher.iv, cipher.doFinal(plaintext.encodeToByteArray()))
    }

    /**
     * @throws java.security.GeneralSecurityException when the key is gone or invalidated or the data was tampered with.
     * @throws IllegalArgumentException when [encoded] is not a payload written by [encrypt].
     */
    fun decrypt(encoded: String): String {
        val (iv, ciphertext) = decodePayload(encoded)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyProvider.getOrCreateKey(), GCMParameterSpec(TAG_LENGTH_BITS, iv))
        return cipher.doFinal(ciphertext).decodeToString()
    }

    fun deleteKey() = keyProvider.deleteKey()

    internal companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val TAG_LENGTH_BITS = 128

        fun encodePayload(
            iv: ByteArray,
            ciphertext: ByteArray,
        ): String {
            require(iv.isNotEmpty() && iv.size <= Byte.MAX_VALUE) { "Invalid IV length: ${iv.size}" }
            return Base64.getEncoder().encodeToString(byteArrayOf(iv.size.toByte()) + iv + ciphertext)
        }

        fun decodePayload(encoded: String): Pair<ByteArray, ByteArray> {
            val bytes = Base64.getDecoder().decode(encoded)
            require(bytes.isNotEmpty()) { "Empty payload" }
            val ivLength = bytes[0].toInt()
            require(ivLength > 0 && bytes.size > 1 + ivLength) { "Truncated payload" }
            return bytes.copyOfRange(1, 1 + ivLength) to bytes.copyOfRange(1 + ivLength, bytes.size)
        }
    }
}
