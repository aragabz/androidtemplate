package com.aragabz.androidtemplate.core.flags

import kotlinx.coroutines.flow.Flow

/**
 * Interface for feature flags.
 */
interface FeatureFlagManager {
    /**
     * Check if a feature is enabled.
     */
    suspend fun isEnabled(feature: Feature): Boolean

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
 * A toggleable feature. Each feature module declares its own flags, e.g.
 * `enum class TodosFlag(override val key: String, override val defaultValue: Boolean) : Feature`.
 * Keys must be unique across the app; they are the persisted override keys.
 */
interface Feature {
    val key: String
    val defaultValue: Boolean
}
