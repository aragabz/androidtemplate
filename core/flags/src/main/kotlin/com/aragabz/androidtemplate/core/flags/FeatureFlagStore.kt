package com.aragabz.androidtemplate.core.flags

import kotlinx.coroutines.flow.Flow

/**
 * Storage interface for persisting feature flag overrides.
 */
public interface FeatureFlagStore {
    /**
     * Get the override value for a feature flag.
     * Returns null if no override is set (should use default).
     */
    public fun getOverride(feature: Feature): Flow<Boolean?>

    /**
     * Set an override value for a feature flag.
     */
    public suspend fun setOverride(
        feature: Feature,
        enabled: Boolean,
    )

    /**
     * Clear the override for a feature flag (revert to default).
     */
    public suspend fun clearOverride(feature: Feature)

    /**
     * Clear all feature flag overrides.
     */
    public suspend fun clearAllOverrides()
}
