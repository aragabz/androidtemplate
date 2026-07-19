package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.R
import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.ErrorRecord
import com.aragabz.androidtemplate.core.common.ui.UiText
import java.io.IOException

/**
 * Maps any [Throwable] into the normalized [AppError] contract.
 */
fun Throwable.toAppError(): AppError =
    when (this) {
        is AppError -> this
        is IOException -> AppError.NetworkError(this)
        else -> AppError.UnknownError(this)
    }

/**
 * Maps an [Exception] or [AppError] to a [UiText] for user display.
 */
fun Throwable.toUiText(): UiText {
    return when (this) {
        is AppError.NetworkError -> UiText.StringResource(R.string.error_network)
        is AppError.HttpError -> {
            when (code) {
                401 -> UiText.StringResource(R.string.error_http_unauthorized)
                404 -> UiText.StringResource(R.string.error_http_not_found)
                in 500..599 -> UiText.StringResource(R.string.error_http_server)
                else -> UiText.StringResource(R.string.error_http_unknown, code)
            }
        }
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
}

/**
 * Converts any [Throwable] to an [ErrorRecord] for structured diagnostics.
 */
fun Throwable.toErrorRecord(): ErrorRecord {
    val normalized = toAppError()
    return when (normalized) {
        is AppError.HttpError -> ErrorRecord(
            category = normalized.category,
            severity = normalized.severity,
            message = normalized.message,
            causeType = normalized::class.java.simpleName,
            isRetryable = normalized.isRetryable,
            httpCode = normalized.code,
        )

        is AppError.NetworkError,
        is AppError.UnknownError,
        -> ErrorRecord(
            category = normalized.category,
            severity = normalized.severity,
            message = normalized.message ?: "Unknown error occurred",
            causeType = normalized::class.java.simpleName,
            isRetryable = normalized.isRetryable,
        )
    }
}
