package com.aragabz.androidtemplate.core.common.result

/**
 * High-level categories for shared error handling and diagnostics.
 */
enum class ErrorCategory {
    NETWORK,
    HTTP,
    UNKNOWN,
}

/**
 * Severity levels used by logging, crash reporting, and UX fallbacks.
 */
enum class ErrorSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL,
}

/**
 * Centralized normalized error record that can be logged or sent to diagnostics systems.
 */
data class ErrorRecord(
    val category: ErrorCategory,
    val severity: ErrorSeverity,
    val message: String,
    val causeType: String,
    val isRetryable: Boolean,
    val httpCode: Int? = null,
)
