package com.aragabz.androidtemplate.core.analytics

/**
 * Minimal performance monitoring abstraction for timing key paths.
 */
interface PerformanceMonitor {
    fun startTrace(name: String)

    fun stopTrace(name: String)

    fun recordMetric(
        name: String,
        valueMs: Long,
    )
}
