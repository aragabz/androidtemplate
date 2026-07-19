package com.aragabz.androidtemplate.core.crash

import com.aragabz.androidtemplate.core.common.result.ErrorSeverity

/**
 * Structured crash/log event with optional diagnostic context.
 */
data class CrashReportEvent(
    val message: String,
    val severity: ErrorSeverity = ErrorSeverity.ERROR,
    val source: String,
    val metadata: Map<String, String> = emptyMap(),
)
