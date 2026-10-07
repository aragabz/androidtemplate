package com.aragabz.androidtemplate.core.common.error

import android.database.sqlite.SQLiteException
import com.aragabz.androidtemplate.core.common.R
import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AuthErrorReason
import com.aragabz.androidtemplate.core.common.result.ErrorRecord
import com.aragabz.androidtemplate.core.common.ui.UiText
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Maps any [Throwable] into the normalized [AppError] contract.
 */
fun Throwable.toAppError(): AppError =
    when (this) {
        is AppError -> this
        is SocketTimeoutException -> AppError.TimeoutError(this)
        is UnknownHostException, is SSLException -> AppError.NetworkError(this)
        is IOException -> AppError.NetworkError(this)
        is SQLiteException -> AppError.DatabaseError(this)
        is SerializationException -> AppError.ParsingError(this)
        is IllegalArgumentException, is IllegalStateException ->
            AppError.ValidationError(message ?: "Validation failed")

        else -> AppError.UnknownError(this)
    }

/**
 * Maps an [Exception] or [AppError] to a [UiText] for user display.
 */
fun Throwable.toUiText(): UiText =
    when (this) {
        is AppError.NetworkError -> UiText.StringResource(R.string.error_network)

        is AppError.TimeoutError -> UiText.StringResource(R.string.error_timeout)

        is AppError.HttpError -> {
            when (code) {
                400 -> UiText.StringResource(R.string.error_http_validation)
                401 -> UiText.StringResource(R.string.error_http_unauthorized)
                403 -> UiText.StringResource(R.string.error_http_forbidden)
                404 -> UiText.StringResource(R.string.error_http_not_found)
                409 -> UiText.StringResource(R.string.error_http_conflict)
                503 -> UiText.StringResource(R.string.error_http_service_unavailable)
                in 500..599 -> UiText.StringResource(R.string.error_http_server)
                else -> UiText.StringResource(R.string.error_http_unknown, code)
            }
        }

        is AppError.DatabaseError -> {
            operation?.let {
                UiText.StringResource(R.string.error_database_operation, it)
            } ?: UiText.StringResource(R.string.error_database)
        }

        is AppError.ValidationError -> {
            field?.let {
                UiText.StringResource(R.string.error_validation_field, it)
            } ?: UiText.StringResource(R.string.error_validation)
        }

        is AppError.AuthError -> {
            when (reason) {
                AuthErrorReason.INVALID_CREDENTIALS ->
                    UiText.StringResource(R.string.error_auth_invalid_credentials)

                AuthErrorReason.TOKEN_EXPIRED ->
                    UiText.StringResource(R.string.error_auth_token_expired)

                AuthErrorReason.SESSION_EXPIRED ->
                    UiText.StringResource(R.string.error_auth_session_expired)

                AuthErrorReason.UNAUTHORIZED ->
                    UiText.StringResource(R.string.error_auth_unauthorized)

                AuthErrorReason.UNKNOWN ->
                    UiText.StringResource(R.string.error_auth_unknown)
            }
        }

        is AppError.ParsingError -> UiText.StringResource(R.string.error_parsing)

        is AppError.UnknownError -> UiText.StringResource(R.string.error_unknown)

        else -> {
            val message = this.localizedMessage ?: this.message
            if (message != null) {
                UiText.DynamicString(message)
            } else {
                UiText.StringResource(R.string.error_unknown)
            }
        }
    }

/**
 * Converts any [Throwable] to an [ErrorRecord] for structured diagnostics.
 */
fun Throwable.toErrorRecord(): ErrorRecord =
    when (val normalized = toAppError()) {
        is AppError.HttpError ->
            ErrorRecord(
                category = normalized.category,
                severity = normalized.severity,
                message = normalized.message,
                causeType = normalized::class.java.simpleName,
                isRetryable = normalized.isRetryable,
                httpCode = normalized.code,
            )

        is AppError.DatabaseError ->
            ErrorRecord(
                category = normalized.category,
                severity = normalized.severity,
                message = normalized.message,
                causeType = normalized::class.java.simpleName,
                isRetryable = normalized.isRetryable,
                operation = normalized.operation,
            )

        is AppError.ValidationError ->
            ErrorRecord(
                category = normalized.category,
                severity = normalized.severity,
                message = normalized.message,
                causeType = normalized::class.java.simpleName,
                isRetryable = normalized.isRetryable,
                field = normalized.field,
            )

        is AppError.NetworkError,
        is AppError.TimeoutError,
        is AppError.AuthError,
        is AppError.ParsingError,
        is AppError.UnknownError,
        ->
            ErrorRecord(
                category = normalized.category,
                severity = normalized.severity,
                message = normalized.message ?: "Unknown error occurred",
                causeType = normalized::class.java.simpleName,
                isRetryable = normalized.isRetryable,
            )
    }
