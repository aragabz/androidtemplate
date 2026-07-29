package com.aragabz.androidtemplate.core.crash

import com.aragabz.androidtemplate.core.common.result.ErrorCategory
import com.aragabz.androidtemplate.core.common.result.ErrorRecord
import com.aragabz.androidtemplate.core.common.result.ErrorSeverity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class TimberCrashReporterTest {
    private val recordingTree = RecordingTree()

    @Before
    fun setUp() {
        Timber.uprootAll()
        Timber.plant(recordingTree)
    }

    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun `logEvent writes severity and metadata to log output`() {
        val reporter = TimberCrashReporter()

        reporter.logEvent(
            CrashReportEvent(
                message = "Sync failed",
                severity = ErrorSeverity.WARNING,
                source = "sync",
                metadata = mapOf("attempt" to "2"),
            ),
        )

        assertTrue(recordingTree.messages.any { it.contains("CrashEvent[sync]") })
        assertTrue(recordingTree.messages.any { it.contains("attempt=2") })
    }

    @Test
    fun `logStructuredError includes category and http code`() {
        val reporter = TimberCrashReporter()

        reporter.logStructuredError(
            record =
                ErrorRecord(
                    category = ErrorCategory.HTTP,
                    severity = ErrorSeverity.ERROR,
                    message = "Server failed",
                    causeType = "HttpError",
                    isRetryable = true,
                    httpCode = 500,
                ),
        )

        assertTrue(recordingTree.messages.any { it.contains("StructuredError[HTTP]") })
        assertTrue(recordingTree.messages.any { it.contains("httpCode=500") })
    }

    private class RecordingTree : Timber.Tree() {
        val messages = mutableListOf<String>()

        override fun log(
            priority: Int,
            tag: String?,
            message: String,
            t: Throwable?,
        ) {
            messages += message
        }
    }
}
