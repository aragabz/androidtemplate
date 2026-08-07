package com.aragabz.androidtemplate.core.flags

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Debug implementation of [FeatureFlagManager].
 *
 * Uses [FeatureFlagStore] to persist feature flag overrides.
 * Overrides take precedence over default values, allowing runtime feature toggling
 * via a debug menu or developer settings.
 */
@Singleton
class DebugFeatureFlagManager
    @Inject
    constructor(
        private val store: FeatureFlagStore,
    ) : FeatureFlagManager {
        override fun isEnabled(feature: Feature): Boolean =
            runBlocking {
                // Check for persisted override, fall back to default
                store.getOverride(feature).first() ?: feature.defaultValue
            }

        override fun observeFeature(feature: Feature): Flow<Boolean> =
            store.getOverride(feature).map { override ->
                override ?: feature.defaultValue
            }

        override suspend fun setOverride(
            feature: Feature,
            enabled: Boolean,
        ) {
            store.setOverride(feature, enabled)
        }

        override suspend fun clearOverride(feature: Feature) {
            store.clearOverride(feature)
        }

        override suspend fun clearAllOverrides() {
            store.clearAllOverrides()
        }
    }
