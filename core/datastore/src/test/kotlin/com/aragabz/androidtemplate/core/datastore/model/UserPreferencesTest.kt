package com.aragabz.androidtemplate.core.datastore.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for UserPreferences, specifically verifying that authToken is excluded from toString()
 * to prevent accidental logging of sensitive authentication data.
 */
class UserPreferencesTest {
    @Test
    fun `toString does not expose authToken`() {
        val preferences = UserPreferences(
            userId = "user123",
            authToken = "secret_token_12345",
            theme = AppTheme.DARK,
            language = "en",
        )

        val stringRepresentation = preferences.toString()

        // Verify sensitive data is not exposed
        assertFalse(
            "authToken should not appear in toString()",
            stringRepresentation.contains("secret_token_12345"),
        )

        // Verify REDACTED marker is present
        assertTrue(
            "toString() should contain REDACTED marker",
            stringRepresentation.contains("***REDACTED***"),
        )
    }

    @Test
    fun `toString includes non-sensitive fields`() {
        val preferences = UserPreferences(
            userId = "user456",
            authToken = "secret_token_67890",
            theme = AppTheme.LIGHT,
            language = "ar",
        )

        val stringRepresentation = preferences.toString()

        // Verify non-sensitive data is included
        assertTrue(stringRepresentation.contains("user456"))
        assertTrue(stringRepresentation.contains("LIGHT"))
        assertTrue(stringRepresentation.contains("ar"))
    }

    @Test
    fun `toString handles null authToken`() {
        val preferences = UserPreferences(
            userId = "user789",
            authToken = null,
            theme = AppTheme.SYSTEM,
            language = "en",
        )

        val stringRepresentation = preferences.toString()

        // Verify REDACTED marker is still present even for null
        assertTrue(
            "toString() should contain REDACTED marker even for null",
            stringRepresentation.contains("***REDACTED***"),
        )
        assertTrue(stringRepresentation.contains("user789"))
    }

    @Test
    fun `toString handles empty authToken`() {
        val preferences = UserPreferences(
            userId = "user999",
            authToken = "",
            theme = AppTheme.DARK,
            language = "fr",
        )

        val stringRepresentation = preferences.toString()

        // Verify empty string is not exposed
        assertTrue(
            "toString() should contain REDACTED marker for empty token",
            stringRepresentation.contains("***REDACTED***"),
        )
    }

    @Test
    fun `toString with long authToken does not expose it`() {
        val longToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

        val preferences = UserPreferences(
            userId = "jwt_user",
            authToken = longToken,
            theme = AppTheme.SYSTEM,
            language = "en",
        )

        val stringRepresentation = preferences.toString()

        // Verify long token is not exposed (check for substring)
        assertFalse(
            "JWT token should not appear in toString()",
            stringRepresentation
                .contains("eyJhbGciOiJIUzI1NiI"),
        )
        assertFalse(
            "JWT signature should not appear in toString()",
            stringRepresentation.contains("SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"),
        )
        assertTrue(stringRepresentation.contains("***REDACTED***"))
    }

    @Test
    fun `data class equality is not affected by toString override`() {
        val prefs1 = UserPreferences(
            userId = "user123",
            authToken = "token123",
            theme = AppTheme.DARK,
            language = "en",
        )

        val prefs2 = UserPreferences(
            userId = "user123",
            authToken = "token123",
            theme = AppTheme.DARK,
            language = "en",
        )

        val prefs3 = UserPreferences(
            userId = "user123",
            authToken = "different_token",
            theme = AppTheme.DARK,
            language = "en",
        )

        // Equality should work normally
        assertEquals(prefs1, prefs2)
        assertTrue(prefs1 == prefs2)
        assertFalse(prefs1 == prefs3)
    }

    @Test
    fun `data class copy is not affected by toString override`() {
        val original = UserPreferences(
            userId = "user123",
            authToken = "token123",
            theme = AppTheme.DARK,
            language = "en",
        )

        val copied = original.copy(language = "ar")

        assertEquals("user123", copied.userId)
        assertEquals("token123", copied.authToken)
        assertEquals(AppTheme.DARK, copied.theme)
        assertEquals("ar", copied.language)

        // toString should still redact authToken
        assertFalse(copied.toString().contains("token123"))
        assertTrue(copied.toString().contains("***REDACTED***"))
    }

    @Test
    fun `toString format is consistent`() {
        val preferences = UserPreferences(
            userId = "user123",
            authToken = "token123",
            theme = AppTheme.DARK,
            language = "en",
        )

        val expected = "UserPreferences(userId=user123, authToken=***REDACTED***, theme=DARK, language=en)"
        assertEquals(expected, preferences.toString())
    }

    @Test
    fun `toString with null userId`() {
        val preferences = UserPreferences(
            userId = null,
            authToken = "token123",
            theme = AppTheme.SYSTEM,
            language = "en",
        )

        val stringRepresentation = preferences.toString()

        assertTrue(stringRepresentation.contains("userId=null"))
        assertFalse(stringRepresentation.contains("token123"))
        assertTrue(stringRepresentation.contains("***REDACTED***"))
    }

    @Test
    fun `toString with default values`() {
        val preferences = UserPreferences()

        val stringRepresentation = preferences.toString()

        assertTrue(stringRepresentation.contains("userId=null"))
        assertTrue(stringRepresentation.contains("***REDACTED***"))
        assertTrue(stringRepresentation.contains("theme=SYSTEM"))
        assertTrue(stringRepresentation.contains("language=en"))
    }

    @Test
    fun `logging UserPreferences does not expose token`() {
        // Simulate a common logging scenario
        val preferences = UserPreferences(
            userId = "log_user",
            authToken = "super_secret_token",
            theme = AppTheme.LIGHT,
            language = "en",
        )

        // This is what would typically be logged
        val logMessage = "User preferences: $preferences"

        // Verify the log message doesn't contain the actual token
        assertFalse(
            "Log message should not contain actual token",
            logMessage.contains("super_secret_token"),
        )
        assertTrue(
            "Log message should contain redacted marker",
            logMessage.contains("***REDACTED***"),
        )
    }

    @Test
    fun `interpolation in strings does not expose token`() {
        val preferences = UserPreferences(
            userId = "string_user",
            authToken = "interpolation_token_123",
            theme = AppTheme.DARK,
            language = "ar",
        )

        // Common string interpolation patterns
        val debug1 = "Debug: $preferences"
        val debug2 = "Preferences: $preferences"
        val debug3 = preferences.toString()

        // None should contain the actual token
        assertFalse(debug1.contains("interpolation_token_123"))
        assertFalse(debug2.contains("interpolation_token_123"))
        assertFalse(debug3.contains("interpolation_token_123"))

        // All should contain the redacted marker
        assertTrue(debug1.contains("***REDACTED***"))
        assertTrue(debug2.contains("***REDACTED***"))
        assertTrue(debug3.contains("***REDACTED***"))
    }
}
