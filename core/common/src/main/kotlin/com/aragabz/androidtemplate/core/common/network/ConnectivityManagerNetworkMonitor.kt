package com.aragabz.androidtemplate.core.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NetworkMonitor using ConnectivityManager.
 *
 * Uses a thread-safe synchronized set to track active networks, as callbacks
 * may be invoked from different threads.
 */
@Singleton
class ConnectivityManagerNetworkMonitor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : NetworkMonitor {
        override val isOnline: Flow<Boolean> =
            callbackFlow {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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
            }.conflate()
    }
