package com.aragabz.androidtemplate.core.flags

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Debug implementation of [FeatureFlagManager].
 * In a real app, this might be backed by Firebase Remote Config or a local debug menu.
 */
@Singleton
class DebugFeatureFlagManager @Inject constructor() : FeatureFlagManager {
    override fun isEnabled(feature: Feature): Boolean {
        // For now, just return the default value.
        // You can add logic here to override these via a debug screen.
        return feature.defaultValue
    }
}
