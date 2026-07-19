package com.aragabz.androidtemplate.core.analytics

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPerformanceMonitor
    @Inject
    constructor() : PerformanceMonitor {
        private val traces = mutableMapOf<String, Long>()

        override fun startTrace(name: String) {
            traces[name] = System.currentTimeMillis()
        }

        override fun stopTrace(name: String) {
            val startedAt = traces.remove(name) ?: return
            val duration = System.currentTimeMillis() - startedAt
            Timber.i("Performance trace: $name durationMs=$duration")
        }

        override fun recordMetric(
            name: String,
            valueMs: Long,
        ) {
            Timber.i("Performance metric: $name valueMs=$valueMs")
        }
    }
