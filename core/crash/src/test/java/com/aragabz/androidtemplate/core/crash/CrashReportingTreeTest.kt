package com.aragabz.androidtemplate.core.crash

import com.aragabz.androidtemplate.core.common.result.ErrorRecord
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.io.IOException

class CrashReportingTreeTest {
    private val reporter = RecordingCrashReporter()

    @Before
    fun setUp() {
        Timber.uprootAll()
        Timber.plant(CrashReportingTree(reporter))
    }

    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun `forwards warnings and errors with their exceptions`() {
        val error = IOException("boom")

        Timber.w("slow sync")
        Timber.e(error, "sync failed")

        assertEquals(listOf("slow sync", "sync failed"), reporter.messages.map { it.substringBefore('\n') })
        assertEquals(listOf<Throwable>(error), reporter.exceptions)
    }

    @Test
    fun `ignores debug logs and the reporter's own output`() {
        Timber.d("debug detail")
        TimberCrashReporter().logException(IOException("reported"))

        assertEquals(emptyList<String>(), reporter.messages)
        assertEquals(emptyList<Throwable>(), reporter.exceptions)
    }

    private class RecordingCrashReporter : CrashReporter {
        val messages = mutableListOf<String>()
        val exceptions = mutableListOf<Throwable>()

        override fun logException(throwable: Throwable) {
            exceptions += throwable
        }

        override fun logMessage(message: String) {
            messages += message
        }

        override fun setCustomKey(
            key: String,
            value: Any,
        ) = Unit

        override fun setUserId(userId: String) = Unit

        override fun logStructuredError(
            record: ErrorRecord,
            throwable: Throwable?,
        ) = Unit

        override fun logEvent(event: CrashReportEvent) = Unit
    }
}
