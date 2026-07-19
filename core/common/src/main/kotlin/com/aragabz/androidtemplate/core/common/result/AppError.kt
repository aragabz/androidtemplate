package com.aragabz.androidtemplate.core.common.result

/**
 * Sealed class representing different types of errors.
 */
sealed class AppError : Throwable() {
    abstract val category: ErrorCategory
    abstract val severity: ErrorSeverity
    open val isRetryable: Boolean = false

    /**
     * HTTP error with status code and message.
     */
    data class HttpError(
        val code: Int,
        override val message: String,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.HTTP
        override val severity: ErrorSeverity =
            if (code in 500..599) ErrorSeverity.ERROR else ErrorSeverity.WARNING
        override val isRetryable: Boolean = code in 500..599
    }

    /**
     * Network connectivity error.
     */
    data class NetworkError(
        override val cause: Throwable,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.NETWORK
        override val severity: ErrorSeverity = ErrorSeverity.ERROR
        override val isRetryable: Boolean = true
        override val message: String = cause.message ?: "Network error occurred"
    }

    /**
     * Unknown error.
     */
    data class UnknownError(
        override val cause: Throwable,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.UNKNOWN
        override val severity: ErrorSeverity = ErrorSeverity.ERROR
        override val message: String = cause.message ?: "Unknown error occurred"
    }
}
