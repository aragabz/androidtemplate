package com.aragabz.androidtemplate.core.sync.worker

import android.content.Context
import com.aragabz.androidtemplate.core.datastore.CachePolicyStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import timber.log.Timber

/**
 * Main worker responsible for background synchronization.
 */
@HiltWorker
class SyncWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val cachePolicyStore: CachePolicyStore,
    ) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result {
            Timber.d("SyncWorker: Starting background synchronization...")

            return try {
                runScheduledCacheCleanup()
                // TODO: Implement actual synchronization logic here
                // This could involve calling use cases from :core:domain or specific repositories

                Timber.d("SyncWorker: Synchronization completed successfully.")
                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "SyncWorker: Synchronization failed.")
                Result.retry()
            }
        }

        private suspend fun runScheduledCacheCleanup() {
            val now = System.currentTimeMillis()
            val lastRun = cachePolicyStore.readLastCleanupRun()
            val cleanupIntervalMillis = TimeUnit.HOURS.toMillis(CLEANUP_INTERVAL_HOURS)

            if (lastRun == null || (now - lastRun) >= cleanupIntervalMillis) {
                cachePolicyStore.markCleanupRun(now)
                Timber.i("SyncWorker: cache cleanup checkpoint updated")
            }
        }

        companion object {
            const val WORK_NAME = "SyncWorker"
            private const val CLEANUP_INTERVAL_HOURS = 6L
        }
    }
