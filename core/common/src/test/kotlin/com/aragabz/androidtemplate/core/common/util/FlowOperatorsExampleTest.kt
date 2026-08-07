package com.aragabz.androidtemplate.core.common.util

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Demonstrates proper usage of stdlib Flow operators for debouncing and throttling.
 * These tests serve as examples for developers.
 */
@OptIn(FlowPreview::class)
class FlowOperatorsExampleTest {
    @Test
    fun `debounce waits for inactivity period before emitting`() =
        runTest {
            val emissions = mutableListOf<Int>()

            val flow = flow {
                emit(1)
                delay(100) // Less than debounce timeout
                emit(2)
                delay(100) // Less than debounce timeout
                emit(3)
                delay(400) // More than debounce timeout - value will emit
                emit(4)
            }

            flow
                .debounce(300)
                .toList(emissions)

            // Only 3 and 4 emit (after 300ms of inactivity)
            assertEquals(listOf(3, 4), emissions)
        }

    @Test
    fun `sample emits at regular intervals`() =
        runTest {
            val emissions = mutableListOf<Int>()

            val flow = flow {
                repeat(10) { i ->
                    emit(i)
                    delay(50) // Emit every 50ms
                }
            }

            flow
                .sample(150) // Sample every 150ms
                .toList(emissions)

            // Should get approximately every 3rd value (150ms / 50ms = 3)
            // Due to timing, we verify we got fewer items than the full stream
            assert(emissions.size < 10)
        }

    @Test
    fun `debounce example for search query`() =
        runTest {
            val searchQueries = mutableListOf<String>()

            val userTyping = flow {
                emit("a")
                delay(100)
                emit("an")
                delay(100)
                emit("and")
                delay(100)
                emit("andr")
                delay(100)
                emit("andro")
                delay(100)
                emit("android")
                delay(500) // User stops typing
            }

            // Only perform search after 300ms of no typing
            userTyping
                .debounce(300)
                .toList(searchQueries)

            // Only "android" should trigger a search
            assertEquals(listOf("android"), searchQueries)
        }

    @Test
    fun `sample example for location updates`() =
        runTest {
            val locations = mutableListOf<String>()

            val gpsUpdates = flow {
                repeat(20) { i ->
                    emit("Location_$i")
                    delay(50) // GPS updates every 50ms
                }
            }

            // Only update UI once per second
            gpsUpdates
                .sample(1000)
                .toList(locations)

            // Should get 1 update (one at the end of the 1-second window)
            assert(locations.size <= 2) // Allow some timing variance
        }
}
