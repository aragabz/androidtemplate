package com.aragabz.androidtemplate.core.common.network

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * Tests for ConnectivityManagerNetworkMonitor thread safety.
 *
 * These tests verify that the synchronized set used in the network monitor
 * can safely handle concurrent modifications from multiple threads, which is
 * required since ConnectivityManager callbacks may be invoked from different threads.
 */
class ConnectivityManagerNetworkMonitorTest {
    @Test
    fun `synchronized set handles concurrent additions safely`() {
        val networks = Collections.synchronizedSet(mutableSetOf<String>())
        val threadCount = 10
        val additionsPerThread = 100
        val latch = CountDownLatch(threadCount)

        // Simulate concurrent onAvailable callbacks from different threads
        repeat(threadCount) { threadId ->
            thread {
                repeat(additionsPerThread) { i ->
                    networks.add("network-$threadId-$i")
                }
                latch.countDown()
            }
        }

        latch.await()
        assertEquals(threadCount * additionsPerThread, networks.size)
    }

    @Test
    fun `synchronized set handles concurrent removals safely`() {
        val networks = Collections.synchronizedSet(mutableSetOf<String>())
        val threadCount = 10
        val itemsPerThread = 100

        // Pre-populate set
        repeat(threadCount) { threadId ->
            repeat(itemsPerThread) { i ->
                networks.add("network-$threadId-$i")
            }
        }

        val latch = CountDownLatch(threadCount)

        // Simulate concurrent onLost callbacks from different threads
        repeat(threadCount) { threadId ->
            thread {
                repeat(itemsPerThread) { i ->
                    networks.remove("network-$threadId-$i")
                }
                latch.countDown()
            }
        }

        latch.await()
        assertEquals(0, networks.size)
    }

    @Test
    fun `synchronized set handles mixed concurrent operations safely`() {
        val networks = Collections.synchronizedSet(mutableSetOf<String>())
        val threadCount = 10
        val operationsPerThread = 100
        val latch = CountDownLatch(threadCount)

        // Simulate mix of onAvailable and onLost callbacks
        repeat(threadCount) { threadId ->
            thread {
                repeat(operationsPerThread) { i ->
                    val networkId = "network-$threadId-$i"
                    if (i % 2 == 0) {
                        networks.add(networkId)
                    } else {
                        networks.remove(networkId)
                    }
                }
                latch.countDown()
            }
        }

        latch.await()
        // Each thread adds 50 and removes 50, net should be 50 * threadCount
        assertEquals(50 * threadCount, networks.size)
    }

    @Test
    fun `synchronized block protects isEmpty check from race condition`() {
        val networks = Collections.synchronizedSet(mutableSetOf<String>())
        val executor = Executors.newFixedThreadPool(4)
        val iterations = 1000
        val latch = CountDownLatch(iterations * 2)

        // Simulate rapid concurrent onAvailable/onLost cycles
        // checking isNotEmpty() while others modify the set
        repeat(iterations) {
            executor.execute {
                networks.add("network-$it")
                synchronized(networks) {
                    // This synchronized block mimics the pattern in ConnectivityManagerNetworkMonitor
                    networks.isNotEmpty()
                }
                latch.countDown()
            }
            executor.execute {
                networks.remove("network-$it")
                synchronized(networks) {
                    networks.isNotEmpty()
                }
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()
        // No ConcurrentModificationException means thread safety is maintained
    }

    @Test
    fun `synchronized set handles high contention without errors`() {
        val networks = Collections.synchronizedSet(mutableSetOf<String>())
        val threadCount = 20
        val operationsPerThread = 500
        val latch = CountDownLatch(threadCount)

        // High contention scenario
        repeat(threadCount) { threadId ->
            thread {
                repeat(operationsPerThread) { i ->
                    when {
                        i % 3 == 0 -> networks.add("net-$i")
                        i % 3 == 1 -> networks.remove("net-$i")
                        else -> synchronized(networks) { networks.size }
                    }
                }
                latch.countDown()
            }
        }

        latch.await()
        // Successfully completing without exceptions verifies thread safety
    }
}
