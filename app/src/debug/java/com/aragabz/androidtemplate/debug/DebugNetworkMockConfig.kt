package com.aragabz.androidtemplate.debug

import timber.log.Timber

/**
 * Entry point reserved for debug-only mock bootstrap.
 */
object DebugNetworkMockConfig {
    fun bootstrap() {
        Timber.d("Debug network mock config initialized")
    }
}
