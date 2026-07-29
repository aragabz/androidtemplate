package com.aragabz.androidtemplate.core.crash

import com.aragabz.androidtemplate.core.common.result.ErrorRecord

/**
 * Interface for crash reporting.
 */
interface CrashReporter {
    /**
     * Log a non-fatal exception.
     */
    fun logException(throwable: Throwable)

    /**
     * Log a message for context.
     */
    fun logMessage(message: String)

    /**
     * Set a custom key-value pair for the crash report.
     */
    fun setCustomKey(
        key: String,
        value: Any,
    )

    /**
     * Set the user ID for the crash report.
     */
    fun setUserId(userId: String)

    /**
     * Log a structured normalized error record for diagnostics systems.
     */
    fun logStructuredError(
        record: ErrorRecord,
        throwable: Throwable? = null,
    )

    /**
     * Log a structured crash event with optional context metadata.
     */
    fun logEvent(event: CrashReportEvent)
}
