package com.aragabz.androidtemplate.core.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.aragabz.androidtemplate.core.common.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NetworkMonitor using ConnectivityManager.
 *
 * The callback is registered once and shared by every collector of [isOnline] in the application scope.
 * Uses a thread-safe synchronized set to track active networks, as callbacks
 * may be invoked from different threads.
 */
@Singleton
class ConnectivityManagerNetworkMonitor
    internal constructor(
        connectivityUpdates: Flow<Boolean>,
        appScope: CoroutineScope,
    ) : NetworkMonitor {
        @Inject
        constructor(
            @ApplicationContext context: Context,
            @ApplicationScope appScope: CoroutineScope,
        ) : this(context.connectivityUpdates(), appScope)

        // One ConnectivityManager callback for all collectors; it is unregistered shortly after the last one leaves.
        override val isOnline: Flow<Boolean> =
            connectivityUpdates
                .distinctUntilChanged()
                .shareIn(appScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), replay = 1)

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }

private fun Context.connectivityUpdates(): Flow<Boolean> =
    callbackFlow {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback =
            object : ConnectivityManager.NetworkCallback() {
                // Use synchronized set for thread-safe access from multiple callback threads
                private val networks = Collections.synchronizedSet(mutableSetOf<Network>())

                override fun onAvailable(network: Network) {
                    networks.add(network)
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    networks.remove(network)
                    // Check size in synchronized block
                    val hasNetworks = synchronized(networks) {
                        networks.isNotEmpty()
                    }
                    trySend(hasNetworks)
                }
            }

        val request =
            NetworkRequest
                .Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit initial state
        val currentNetwork = connectivityManager.activeNetwork
        val isConnected =
            currentNetwork?.let {
                val capabilities = connectivityManager.getNetworkCapabilities(it)
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            } ?: false
        trySend(isConnected)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
