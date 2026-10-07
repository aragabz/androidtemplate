package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.ErrorRecord
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
        is SerializationException -> AppError.ParsingError(this)
        // IllegalArgumentException/IllegalStateException (require/check) are programming errors, not user input
        // problems; create AppError.ValidationError explicitly for real validation failures.
        else -> if (isSqliteException()) AppError.DatabaseError(this) else AppError.UnknownError(this)
    }

/**
 * Room and SQLite throw android.database.sqlite.SQLiteException (or a subclass). It is matched by class name
 * so this module stays pure Kotlin/JVM.
 */
private fun Throwable.isSqliteException(): Boolean =
    generateSequence<Class<*>>(javaClass) { it.superclass }.any { it.name == SQLITE_EXCEPTION_CLASS }

private const val SQLITE_EXCEPTION_CLASS = "android.database.sqlite.SQLiteException"

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
