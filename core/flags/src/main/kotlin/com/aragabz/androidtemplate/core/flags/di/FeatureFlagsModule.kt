package com.aragabz.androidtemplate.core.flags.di

import com.aragabz.androidtemplate.core.flags.DebugFeatureFlagManager
import com.aragabz.androidtemplate.core.flags.FeatureFlagManager
import com.aragabz.androidtemplate.core.flags.FeatureFlagStore
import com.aragabz.androidtemplate.core.flags.FeatureFlagStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module providing feature flag dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeatureFlagsModule {
    @Binds
    abstract fun bindFeatureFlagManager(impl: DebugFeatureFlagManager): FeatureFlagManager

    @Binds
    abstract fun bindFeatureFlagStore(impl: FeatureFlagStoreImpl): FeatureFlagStore
}
