package com.aragabz.androidtemplate.core.crash

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
    fun setCustomKey(key: String, value: Any)

    /**
     * Set the user ID for the crash report.
     */
    fun setUserId(userId: String)
}
