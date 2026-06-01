package com.aragabz.androidtemplate.core.common.result

/**
 * Sealed class representing different types of errors.
 */
sealed class AppError : Throwable() {
    /**
     * HTTP error with status code and message.
     */
    data class HttpError(
        val code: Int,
        override val message: String,
    ) : AppError()

    /**
     * Network connectivity error.
     */
    data class NetworkError(
        override val cause: Throwable,
    ) : AppError() {
        override val message: String = cause.message ?: "Network error occurred"
    }

    /**
     * Unknown error.
     */
    data class UnknownError(
        override val cause: Throwable,
    ) : AppError() {
        override val message: String = cause.message ?: "Unknown error occurred"
    }
}
