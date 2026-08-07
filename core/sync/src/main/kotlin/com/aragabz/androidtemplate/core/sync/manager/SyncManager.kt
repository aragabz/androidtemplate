package com.aragabz.androidtemplate.core.sync.manager

import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit

/**
 * Interface for managing background synchronization tasks.
 */
interface SyncManager {
    /**
     * Schedules a periodic synchronization task.
     * @param interval The repeat interval.
     * @param timeUnit The unit of time for the interval.
     */
    fun schedulePeriodicSync(
        interval: Long = 15,
        timeUnit: TimeUnit = TimeUnit.MINUTES,
    )

    /**
     * Triggers an immediate one-time synchronization.
     */
    fun triggerImmediateSync()

    /**
     * Cancels all scheduled synchronization tasks.
     */
    fun cancelAllSync()
}

/**
 * Standard constraints for synchronization tasks.
 *
 * These constraints ensure sync only runs when:
 * - Device is on unmetered network (Wi-Fi, not mobile data)
 * - Battery is not low (above critical level)
 * - Device has sufficient storage space
 *
 * This prevents sync from draining battery on mobile data or when battery is low.
 */
val SyncConstraints =
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .build()

/**
 * Relaxed constraints for user-initiated sync.
 *
 * Less restrictive - allows sync on any network (including metered/mobile data)
 * but still respects battery and storage constraints.
 */
val SyncConstraintsRelaxed =
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .build()
