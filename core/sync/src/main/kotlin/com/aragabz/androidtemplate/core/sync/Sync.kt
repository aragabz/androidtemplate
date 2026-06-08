package com.aragabz.androidtemplate.core.sync

import com.aragabz.androidtemplate.core.sync.manager.SyncManager

/**
 * Entry point for initializing background synchronization.
 */
object Sync {
    /**
     * Initializes the periodic synchronization task.
     */
    fun initialize(syncManager: SyncManager) {
        syncManager.schedulePeriodicSync()
    }
}
