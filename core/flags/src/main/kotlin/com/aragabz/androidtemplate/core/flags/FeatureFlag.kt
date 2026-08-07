package com.aragabz.androidtemplate.core.flags

import kotlinx.coroutines.flow.Flow

/**
 * Interface for feature flags.
 */
interface FeatureFlagManager {
    /**
     * Check if a feature is enabled.
     */
    fun isEnabled(feature: Feature): Boolean

    /**
     * Get a flow of the current enabled state for a feature.
     * Useful for observing feature flag changes in Compose or ViewModels.
     */
    fun observeFeature(feature: Feature): Flow<Boolean>

    /**
     * Set an override value for a feature flag.
     * Useful for debug menus or developer settings.
     */
    suspend fun setOverride(
        feature: Feature,
        enabled: Boolean,
    )

    /**
     * Clear the override for a feature flag (revert to default).
     */
    suspend fun clearOverride(feature: Feature)

    /**
     * Clear all feature flag overrides.
     */
    suspend fun clearAllOverrides()
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
