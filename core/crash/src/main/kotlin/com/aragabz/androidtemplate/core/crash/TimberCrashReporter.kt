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
        // Tagged so CrashReportingTree never forwards the reporter's own output back to it.
        private val log: Timber.Tree get() = Timber.tag(TAG)

        override fun logException(throwable: Throwable) {
            log.e(throwable)
        }

        override fun logMessage(message: String) {
            log.d("CrashReport: $message")
        }

        override fun setCustomKey(
            key: String,
            value: Any,
        ) {
            log.d("CrashReport Key: $key = $value")
        }

        override fun setUserId(userId: String) {
            log.d("CrashReport UserID: $userId")
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
                log.e(throwable, msg)
            } else {
                log.e(msg)
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
                ErrorSeverity.INFO -> log.i(message)
                ErrorSeverity.WARNING -> log.w(message)
                ErrorSeverity.ERROR,
                ErrorSeverity.CRITICAL,
                -> log.e(message)
            }
        }

        companion object {
            const val TAG = "CrashReporter"
        }
    }
