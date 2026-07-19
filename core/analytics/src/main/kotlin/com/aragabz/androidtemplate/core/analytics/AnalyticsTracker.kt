package com.aragabz.androidtemplate.core.analytics

/**
 * Abstraction for analytics tracking.
 */
interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)

    fun setUserProperty(
        key: String,
        value: String,
    )
}
