package com.aragabz.androidtemplate.core.analytics

/**
 * Analytics event payload used across features.
 */
data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, String> = emptyMap(),
)
