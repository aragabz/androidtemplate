package com.aragabz.androidtemplate.core.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Hilt module providing coroutine dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
public object CoroutineDispatchersModule {
    @Provides
    @IoDispatcher
    public fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    public fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    public fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
