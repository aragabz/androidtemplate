package com.aragabz.androidtemplate.core.analytics

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimberAnalyticsTracker
    @Inject
    constructor() : AnalyticsTracker {
        override fun track(event: AnalyticsEvent) {
            val properties =
                if (event.properties.isEmpty()) {
                    "{}"
                } else {
                    event.properties.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
                }

            Timber.i("Analytics event: ${event.name} properties=$properties")
        }

        override fun setUserProperty(
            key: String,
            value: String,
        ) {
            Timber.i("Analytics user property: $key=$value")
        }
    }
