package com.aragabz.androidtemplate.core.common.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Checks if the device currently has network connectivity.
 * @return true if online, false otherwise
 */
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val network = connectivityManager?.activeNetwork
    val capabilities = connectivityManager?.getNetworkCapabilities(network)

    return capabilities?.let {
        it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } ?: false
}
