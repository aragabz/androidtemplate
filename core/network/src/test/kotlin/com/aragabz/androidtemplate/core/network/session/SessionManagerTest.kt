package com.aragabz.androidtemplate.core.network.session

import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for SessionManager — verifies lifecycle-scoped 401 handling.
 */
class SessionManagerTest {
    @Test
    fun `notifyUnauthorized emits event to collectors`() =
        runTest {
            val sessionManager = SessionManager()
            var eventReceived = false

            // Collect in background
            val collectJob =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    sessionManager.onUnauthorized.collect {
                        eventReceived = true
                    }
                }

            // Trigger 401
            sessionManager.notifyUnauthorized()

            // Verify event was received
            assertTrue("Expected unauthorized event to be received", eventReceived)

            collectJob.cancel()
        }

    @Test
    fun `notifyUnauthorized is non-suspending`() {
        val sessionManager = SessionManager()

        // This should not require suspend context
        // If it compiles, the test passes
        sessionManager.notifyUnauthorized()
    }

    @Test
    fun `multiple notifyUnauthorized calls emit multiple events`() =
        runTest {
            val sessionManager = SessionManager()
            var eventCount = 0

            val collectJob =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    sessionManager.onUnauthorized.collect {
                        eventCount++
                    }
                }

            // Trigger multiple 401s
            sessionManager.notifyUnauthorized()
            sessionManager.notifyUnauthorized()
            sessionManager.notifyUnauthorized()

            // All events should be received
            assertTrue("Expected 3 events, got $eventCount", eventCount == 3)

            collectJob.cancel()
        }

    @Test
    fun `notifyUnauthorized with no collectors does not block`() =
        runTest {
            val sessionManager = SessionManager()

            // Should not throw or block even with no collectors
            sessionManager.notifyUnauthorized()
            sessionManager.notifyUnauthorized()

            // Test passes if we reach here without hanging
        }

    @Test
    fun `SharedFlow has no replay - late collectors miss previous events`() =
        runTest {
            val sessionManager = SessionManager()

            // Emit before collector
            sessionManager.notifyUnauthorized()

            var eventReceived = false
            val collectJob =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    sessionManager.onUnauthorized.collect {
                        eventReceived = true
                    }
                }

            // Late collector should not receive the event (no replay)
            assertFalse("Expected late collector to miss event (no replay)", eventReceived)

            collectJob.cancel()
        }

    @Test
    fun `collector cancelled before event does not receive it`() =
        runTest {
            val sessionManager = SessionManager()
            var eventReceived = false

            val collectJob =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    sessionManager.onUnauthorized.collect {
                        eventReceived = true
                    }
                }

            // Cancel collector before emitting
            collectJob.cancel()

            // Emit event
            sessionManager.notifyUnauthorized()

            // Cancelled collector should not receive event
            assertFalse("Expected cancelled collector to not receive event", eventReceived)
        }
}
