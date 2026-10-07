package com.aragabz.androidtemplate.core.datastore

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.security.GeneralSecurityException
import java.util.Base64

class TokenCipherTest {
    private val keyProvider = FakeSecretKeyProvider()
    private val cipher = TokenCipher(keyProvider)

    @Test
    fun `decrypt returns the encrypted token`() {
        assertEquals("token-123", cipher.decrypt(cipher.encrypt("token-123")))
    }

    @Test
    fun `every encryption uses a fresh 12-byte IV`() {
        val first = TokenCipher.decodePayload(cipher.encrypt("token"))
        val second = TokenCipher.decodePayload(cipher.encrypt("token"))

        assertEquals(12, first.first.size)
        assertNotEquals(first.first.toList(), second.first.toList())
        assertNotEquals(first.second.toList(), second.second.toList())
    }

    @Test
    fun `payload stores the IV length, IV and ciphertext`() {
        val iv = byteArrayOf(1, 2, 3)
        val ciphertext = byteArrayOf(9, 8)

        val encoded = TokenCipher.encodePayload(iv, ciphertext)

        assertArrayEquals(byteArrayOf(3, 1, 2, 3, 9, 8), Base64.getDecoder().decode(encoded))
        val (decodedIv, decodedCiphertext) = TokenCipher.decodePayload(encoded)
        assertArrayEquals(iv, decodedIv)
        assertArrayEquals(ciphertext, decodedCiphertext)
    }

    @Test
    fun `decodePayload rejects malformed payloads`() {
        assertThrows(IllegalArgumentException::class.java) { TokenCipher.decodePayload("not base64!") }
        assertThrows(IllegalArgumentException::class.java) { TokenCipher.decodePayload("") }
        // IV length 12 but only 3 bytes follow.
        val truncated = Base64.getEncoder().encodeToString(byteArrayOf(12, 1, 2, 3))
        assertThrows(IllegalArgumentException::class.java) { TokenCipher.decodePayload(truncated) }
    }

    @Test
    fun `tampered ciphertext fails authentication`() {
        val (iv, ciphertext) = TokenCipher.decodePayload(cipher.encrypt("token"))
        ciphertext[0] = (ciphertext[0].toInt() xor 1).toByte()

        assertThrows(GeneralSecurityException::class.java) {
            cipher.decrypt(TokenCipher.encodePayload(iv, ciphertext))
        }
    }

    @Test
    fun `a new key cannot decrypt tokens written with the old one`() {
        val encrypted = cipher.encrypt("token")
        keyProvider.deleteKey()

        assertThrows(GeneralSecurityException::class.java) { cipher.decrypt(encrypted) }
    }
}
