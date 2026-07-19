package com.aragabz.androidtemplate.core.analytics.di

import com.aragabz.androidtemplate.core.analytics.AnalyticsTracker
import com.aragabz.androidtemplate.core.analytics.DefaultPerformanceMonitor
import com.aragabz.androidtemplate.core.analytics.PerformanceMonitor
import com.aragabz.androidtemplate.core.analytics.TimberAnalyticsTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun bindAnalyticsTracker(impl: TimberAnalyticsTracker): AnalyticsTracker

    @Binds
    abstract fun bindPerformanceMonitor(impl: DefaultPerformanceMonitor): PerformanceMonitor
}
