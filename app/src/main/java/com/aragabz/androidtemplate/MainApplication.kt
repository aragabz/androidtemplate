package com.aragabz.androidtemplate

import android.app.Application
import android.os.SystemClock
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aragabz.androidtemplate.core.analytics.AnalyticsEvent
import com.aragabz.androidtemplate.core.analytics.AnalyticsTracker
import com.aragabz.androidtemplate.core.analytics.PerformanceMonitor
import com.aragabz.androidtemplate.core.crash.CrashReporter
import com.aragabz.androidtemplate.core.crash.CrashReportingTree
import com.aragabz.androidtemplate.core.sync.Sync
import com.aragabz.androidtemplate.core.sync.manager.SyncManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Application class with Hilt initialization.
 */
@HiltAndroidApp
class MainApplication :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    @Inject
    lateinit var crashReporter: CrashReporter

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration
            .Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Debug logs go to logcat; release warnings and errors go to the crash reporter.
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree(crashReporter))

        val startupStart = SystemClock.elapsedRealtime()

        performanceMonitor.startTrace("app_startup")

        // Initialize background synchronization
        Sync.initialize(syncManager)

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
