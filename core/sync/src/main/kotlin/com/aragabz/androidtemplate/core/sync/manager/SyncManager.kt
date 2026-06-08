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
 */
val SyncConstraints =
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()
