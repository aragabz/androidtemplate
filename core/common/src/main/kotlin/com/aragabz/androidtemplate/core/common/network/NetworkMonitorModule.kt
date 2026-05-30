package com.aragabz.androidtemplate.core.common.network

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module providing NetworkMonitor.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkMonitorModule {

    @Binds
    abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}
