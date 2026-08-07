package com.aragabz.androidtemplate.core.crash

import com.aragabz.androidtemplate.core.common.result.ErrorRecord
import com.aragabz.androidtemplate.core.common.result.ErrorSeverity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Timber implementation of [CrashReporter].
 * This is a fallback implementation that logs to Timber.
 * In a real app, you would swap this with Firebase Crashlytics or similar.
 */
@Singleton
class TimberCrashReporter
    @Inject
    constructor() : CrashReporter {
        override fun logException(throwable: Throwable) {
            Timber.e(throwable)
        }

        override fun logMessage(message: String) {
            Timber.d("CrashReport: $message")
        }

        override fun setCustomKey(
            key: String,
            value: Any,
        ) {
            Timber.d("CrashReport Key: $key = $value")
        }

        override fun setUserId(userId: String) {
            Timber.d("CrashReport UserID: $userId")
        }

        override fun logStructuredError(
            record: ErrorRecord,
            throwable: Throwable?,
        ) {
            val msg =
                buildString {
                    append("StructuredError[")
                    append(record.category)
                    append("] severity=")
                    append(record.severity)
                    append(" retryable=")
                    append(record.isRetryable)
                    record.httpCode?.let {
                        append(" httpCode=")
                        append(it)
                    }
                    append(" cause=")
                    append(record.causeType)
                    append(" message=")
                    append(record.message)
                }

            if (throwable != null) {
                Timber.e(throwable, msg)
            } else {
                Timber.e(msg)
            }
        }

        override fun logEvent(event: CrashReportEvent) {
            val metadata =
                if (event.metadata.isEmpty()) {
                    "{}"
                } else {
                    event.metadata.entries.joinToString(
                        ", ",
                    ) { "${it.key}=${it.value}" }
                }

            val message =
                "CrashEvent[${event.source}] severity=${event.severity} message=${event.message} metadata=$metadata"
            when (event.severity) {
                ErrorSeverity.INFO -> Timber.i(message)
                ErrorSeverity.WARNING -> Timber.w(message)
                ErrorSeverity.ERROR,
                ErrorSeverity.CRITICAL,
                -> Timber.e(message)
            }
        }
    }
