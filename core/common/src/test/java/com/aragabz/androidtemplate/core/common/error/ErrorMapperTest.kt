package com.aragabz.androidtemplate.core.common.error

import android.database.sqlite.SQLiteException
import com.aragabz.androidtemplate.core.common.R
import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AuthErrorReason
import com.aragabz.androidtemplate.core.common.result.ErrorCategory
import com.aragabz.androidtemplate.core.common.ui.UiText
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorMapperTest {
    @Test
    fun `toAppError maps IOException to NetworkError`() {
        val throwable = IOException("network down")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.NetworkError)
        assertEquals(ErrorCategory.NETWORK, mapped.category)
        assertTrue(mapped.isRetryable)
    }

    @Test
    fun `toAppError maps SocketTimeoutException to TimeoutError`() {
        val throwable = SocketTimeoutException("timeout")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.TimeoutError)
        assertEquals(ErrorCategory.TIMEOUT, mapped.category)
        assertTrue(mapped.isRetryable)
    }

    @Test
    fun `toAppError maps SQLiteException to DatabaseError`() {
        val throwable = SQLiteException("database locked")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.DatabaseError)
        assertEquals(ErrorCategory.DATABASE, mapped.category)
    }

    @Test
    fun `toAppError maps SerializationException to ParsingError`() {
        val throwable = SerializationException("malformed JSON")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.ParsingError)
        assertEquals(ErrorCategory.PARSING, mapped.category)
    }

    @Test
    fun `toAppError maps IllegalArgumentException to UnknownError`() {
        val throwable = IllegalArgumentException("invalid input")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.UnknownError)
        assertEquals(ErrorCategory.UNKNOWN, mapped.category)
        assertEquals(throwable, mapped.cause)
    }

    @Test
    fun `toAppError maps IllegalStateException to UnknownError`() {
        val throwable = IllegalStateException("bad state")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.UnknownError)
        assertEquals(ErrorCategory.UNKNOWN, mapped.category)
    }

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
            cause = SQLiteException("error"),
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
        val throwable = AppError.ParsingError(SerializationException("malformed"))

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_parsing, resource.resId)
    }

    @Test
    fun `toErrorRecord includes http code and retryable flag`() {
        val throwable = AppError.HttpError(503, "service unavailable")

        val record = throwable.toErrorRecord()

        assertEquals(ErrorCategory.HTTP, record.category)
        assertEquals(503, record.httpCode)
        assertTrue(record.isRetryable)
        assertEquals("HttpError", record.causeType)
    }

    @Test
    fun `toErrorRecord includes database operation`() {
        val throwable = AppError.DatabaseError(
            cause = SQLiteException("error"),
            operation = "delete",
        )

        val record = throwable.toErrorRecord()

        assertEquals(ErrorCategory.DATABASE, record.category)
        assertEquals("delete", record.operation)
        assertEquals("DatabaseError", record.causeType)
    }

    @Test
    fun `toErrorRecord includes validation field`() {
        val throwable = AppError.ValidationError("Invalid", "username")

        val record = throwable.toErrorRecord()

        assertEquals(ErrorCategory.VALIDATION, record.category)
        assertEquals("username", record.field)
        assertEquals("ValidationError", record.causeType)
    }
}
