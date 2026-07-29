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
     * Database error (insert, query, delete failures).
     */
    data class DatabaseError(
        override val cause: Throwable,
        val operation: String? = null,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.DATABASE
        override val severity: ErrorSeverity = ErrorSeverity.ERROR
        override val message: String =
            operation?.let { "Database $it failed" }
                ?: cause.message
                ?: "Database operation failed"
    }

    /**
     * Validation error (invalid input, business rule violation).
     */
    data class ValidationError(
        override val message: String,
        val field: String? = null,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.VALIDATION
        override val severity: ErrorSeverity = ErrorSeverity.WARNING
    }

    /**
     * Authentication error (invalid credentials, token expired).
     */
    data class AuthError(
        override val message: String,
        val reason: AuthErrorReason = AuthErrorReason.UNKNOWN,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.AUTH
        override val severity: ErrorSeverity = ErrorSeverity.WARNING
    }

    /**
     * Timeout error (operation took too long).
     */
    data class TimeoutError(
        override val cause: Throwable? = null,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.TIMEOUT
        override val severity: ErrorSeverity = ErrorSeverity.WARNING
        override val isRetryable: Boolean = true
        override val message: String = "Operation timed out"
    }

    /**
     * Parsing error (JSON, XML, protobuf deserialization failed).
     */
    data class ParsingError(
        override val cause: Throwable,
    ) : AppError() {
        override val category: ErrorCategory = ErrorCategory.PARSING
        override val severity: ErrorSeverity = ErrorSeverity.ERROR
        override val message: String = "Failed to parse response"
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

/**
 * Specific authentication error reasons.
 */
enum class AuthErrorReason {
    INVALID_CREDENTIALS,
    TOKEN_EXPIRED,
    SESSION_EXPIRED,
    UNAUTHORIZED,
    UNKNOWN,
}
