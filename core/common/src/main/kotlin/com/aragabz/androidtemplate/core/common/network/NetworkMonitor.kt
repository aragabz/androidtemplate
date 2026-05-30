package com.aragabz.androidtemplate.core.common.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring network connectivity.
 */
interface NetworkMonitor {
    /**
     * Flow emitting network connectivity status.
     * Emits true when online, false when offline.
     */
    val isOnline: Flow<Boolean>
}
