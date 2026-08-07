package com.aragabz.androidtemplate.feature.auth.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for AuthSession, verifying that token is excluded from toString().
 */
class AuthSessionTest {

    @Test
    fun `toString does not expose token`() {
        val session = AuthSession(
            userId = "user123",
            token = "secret_jwt_token_12345"
        )

        val stringRepresentation = session.toString()

        assertFalse(
            "token should not appear in toString()",
            stringRepresentation.contains("secret_jwt_token_12345")
        )
        assertTrue(
            "toString() should contain REDACTED marker",
            stringRepresentation.contains("***REDACTED***")
        )
    }

    @Test
    fun `toString includes non-sensitive fields`() {
        val session = AuthSession(
            userId = "user456",
            token = "another_secret_token"
        )

        val stringRepresentation = session.toString()

        assertTrue(stringRepresentation.contains("user456"))
        assertTrue(stringRepresentation.contains("isAuthenticated=true"))
    }

    @Test
    fun `toString handles null values`() {
        val session = AuthSession(userId = null, token = null)

        val stringRepresentation = session.toString()

        assertTrue(stringRepresentation.contains("userId=null"))
        assertTrue(stringRepresentation.contains("***REDACTED***"))
        assertTrue(stringRepresentation.contains("isAuthenticated=false"))
    }

    @Test
    fun `isAuthenticated is true when both userId and token are present`() {
        val session = AuthSession(
            userId = "user123",
            token = "token123"
        )

        assertTrue(session.isAuthenticated)
    }

    @Test
    fun `isAuthenticated is false when userId is null`() {
        val session = AuthSession(
            userId = null,
            token = "token123"
        )

        assertFalse(session.isAuthenticated)
    }

    @Test
    fun `isAuthenticated is false when token is null`() {
        val session = AuthSession(
            userId = "user123",
            token = null
        )

        assertFalse(session.isAuthenticated)
    }

    @Test
    fun `isAuthenticated is false when userId is blank`() {
        val session = AuthSession(
            userId = "  ",
            token = "token123"
        )

        assertFalse(session.isAuthenticated)
    }

    @Test
    fun `data class equality is not affected by toString override`() {
        val session1 = AuthSession(userId = "user123", token = "token123")
        val session2 = AuthSession(userId = "user123", token = "token123")
        val session3 = AuthSession(userId = "user123", token = "different_token")

        assertEquals(session1, session2)
        assertTrue(session1 == session2)
        assertFalse(session1 == session3)
    }

    @Test
    fun `data class copy is not affected by toString override`() {
        val original = AuthSession(userId = "user123", token = "token123")
        val copied = original.copy(userId = "user456")

        assertEquals("user456", copied.userId)
        assertEquals("token123", copied.token)
        
        // toString should still redact token
        assertFalse(copied.toString().contains("token123"))
        assertTrue(copied.toString().contains("***REDACTED***"))
    }

    @Test
    fun `logging AuthSession does not expose token`() {
        val session = AuthSession(
            userId = "log_user",
            token = "super_secret_token"
        )

        val logMessage = "Auth session: $session"

        assertFalse(logMessage.contains("super_secret_token"))
        assertTrue(logMessage.contains("***REDACTED***"))
    }
}
