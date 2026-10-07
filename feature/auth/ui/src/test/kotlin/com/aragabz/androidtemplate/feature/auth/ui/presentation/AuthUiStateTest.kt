package com.aragabz.androidtemplate.feature.auth.ui.presentation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for AuthUiState, verifying that password is excluded from toString().
 */
class AuthUiStateTest {
    @Test
    fun `toString does not expose password`() {
        val state = AuthUiState(
            email = "user@example.com",
            password = "MySecretPassword123!",
            isSignUpMode = false,
        )

        val stringRepresentation = state.toString()

        assertFalse(
            "password should not appear in toString()",
            stringRepresentation.contains("MySecretPassword123!"),
        )
        assertTrue(
            "toString() should contain REDACTED marker",
            stringRepresentation.contains("***REDACTED***"),
        )
    }

    @Test
    fun `toString includes non-sensitive fields`() {
        val state = AuthUiState(
            email = "test@example.com",
            password = "secret123",
            isSignUpMode = true,
            isAuthenticated = false,
            isLoading = true,
        )

        val stringRepresentation = state.toString()

        assertTrue(stringRepresentation.contains("test@example.com"))
        assertTrue(stringRepresentation.contains("isSignUpMode=true"))
        assertTrue(stringRepresentation.contains("isAuthenticated=false"))
        assertTrue(stringRepresentation.contains("isLoading=true"))
        assertFalse(stringRepresentation.contains("secret123"))
    }

    @Test
    fun `toString handles empty password`() {
        val state = AuthUiState(
            email = "user@example.com",
            password = "",
            isSignUpMode = false,
        )

        val stringRepresentation = state.toString()

        assertTrue(stringRepresentation.contains("***REDACTED***"))
        assertTrue(stringRepresentation.contains("user@example.com"))
    }

    @Test
    fun `logging AuthUiState does not expose password`() {
        val state = AuthUiState(
            email = "log_user@example.com",
            password = "SuperSecretPassword!",
            isLoading = true,
        )

        val logMessage = "UI State: $state"

        assertFalse(logMessage.contains("SuperSecretPassword!"))
        assertTrue(logMessage.contains("***REDACTED***"))
        assertTrue(logMessage.contains("log_user@example.com"))
    }

    @Test
    fun `data class copy does not affect toString override`() {
        val original = AuthUiState(
            email = "user@example.com",
            password = "password123",
        )

        val copied = original.copy(isLoading = true)

        assertFalse(copied.toString().contains("password123"))
        assertTrue(copied.toString().contains("***REDACTED***"))
    }
}
