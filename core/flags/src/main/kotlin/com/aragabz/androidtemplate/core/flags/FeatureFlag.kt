package com.aragabz.androidtemplate.core.flags

/**
 * Interface for feature flags.
 */
interface FeatureFlagManager {
    /**
     * Check if a feature is enabled.
     */
    fun isEnabled(feature: Feature): Boolean
}

/**
 * List of features that can be toggled.
 */
enum class Feature(
    val key: String,
    val defaultValue: Boolean,
) {
    NEW_TODO_DESIGN("new_todo_design", false),
    EXPERIMENTAL_SYNC("experimental_sync", true),
}
