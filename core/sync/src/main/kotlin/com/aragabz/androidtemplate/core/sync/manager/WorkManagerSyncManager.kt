package com.aragabz.androidtemplate.core.sync.manager

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aragabz.androidtemplate.core.sync.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerSyncManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : SyncManager {
        private val workManager = WorkManager.getInstance(context)

        override fun schedulePeriodicSync(
            interval: Long,
            timeUnit: TimeUnit,
        ) {
            val workRequest =
                PeriodicWorkRequestBuilder<SyncWorker>(interval, timeUnit)
                    .setConstraints(SyncConstraints)
                    .build()

            workManager.enqueueUniquePeriodicWork(
                SyncWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )
        }

        override fun triggerImmediateSync() {
            val workRequest =
                OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(SyncConstraintsRelaxed)
                    .build()

            workManager.enqueueUniqueWork(
                "${SyncWorker.WORK_NAME}_one_time",
                ExistingWorkPolicy.REPLACE,
                workRequest,
            )
        }

        override fun cancelAllSync() {
            workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
            workManager.cancelUniqueWork("${SyncWorker.WORK_NAME}_one_time")
        }
    }
