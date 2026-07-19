package com.aragabz.androidtemplate

import android.app.Application
import android.os.SystemClock
import com.aragabz.androidtemplate.core.analytics.AnalyticsEvent
import com.aragabz.androidtemplate.core.analytics.AnalyticsTracker
import com.aragabz.androidtemplate.core.analytics.PerformanceMonitor
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aragabz.androidtemplate.core.network.BuildConfig
import com.aragabz.androidtemplate.core.sync.Sync
import com.aragabz.androidtemplate.core.sync.manager.SyncManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Application class with Hilt initialization.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val startupStart = SystemClock.elapsedRealtime()

        performanceMonitor.startTrace("app_startup")

        // Initialize background synchronization
        Sync.initialize(syncManager)

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val startupDuration = SystemClock.elapsedRealtime() - startupStart
        performanceMonitor.recordMetric("app_startup_duration", startupDuration)
        performanceMonitor.stopTrace("app_startup")

        analyticsTracker.track(
            AnalyticsEvent(
                name = "app_started",
                properties =
                    mapOf(
                        "build_type" to if (BuildConfig.DEBUG) "debug" else "release",
                        "startup_ms" to startupDuration.toString(),
                    ),
            ),
        )
    }
}
