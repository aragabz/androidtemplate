package com.aragabz.androidtemplate.core.ui.text

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.result.AuthErrorReason
import com.aragabz.androidtemplate.core.ui.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorUiTextTest {
    @Test
    fun `toUiText maps http 401 to unauthorized message`() {
        val throwable = AppError.HttpError(401, "unauthorized")

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_http_unauthorized, resource.resId)
    }

    @Test
    fun `toUiText maps http 404 to not found message`() {
        val throwable = AppError.HttpError(404, "not found")

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_http_not_found, resource.resId)
    }

    @Test
    fun `toUiText maps http 503 to service unavailable message`() {
        val throwable = AppError.HttpError(503, "service unavailable")

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_http_service_unavailable, resource.resId)
    }

    @Test
    fun `toUiText maps database error with operation`() {
        val throwable = AppError.DatabaseError(
            cause = IllegalStateException("error"),
            operation = "insert",
        )

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_database_operation, resource.resId)
        assertEquals("insert", resource.args[0])
    }

    @Test
    fun `toUiText maps validation error with field`() {
        val throwable = AppError.ValidationError("Invalid email", "email")

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_validation_field, resource.resId)
        assertEquals("email", resource.args[0])
    }

    @Test
    fun `toUiText maps auth error with invalid credentials reason`() {
        val throwable = AppError.AuthError(
            "Invalid credentials",
            AuthErrorReason.INVALID_CREDENTIALS,
        )

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_auth_invalid_credentials, resource.resId)
    }

    @Test
    fun `toUiText maps timeout error`() {
        val throwable = AppError.TimeoutError()

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_timeout, resource.resId)
    }

    @Test
    fun `toUiText maps parsing error`() {
        val throwable = AppError.ParsingError(IllegalStateException("malformed"))

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_parsing, resource.resId)
    }

    @Test
    fun `errorUiText is null unless the result is an error`() {
        assertNull(AppResult.Success(1).errorUiText)
        assertNull(AppResult.Loading.errorUiText)
        assertEquals(
            UiText.StringResource(R.string.error_timeout),
            AppResult.Error(AppError.TimeoutError()).errorUiText,
        )
    }
}
