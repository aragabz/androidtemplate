package com.aragabz.androidtemplate.core.crash

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CrashReporterModule {
    @Binds
    @Singleton
    abstract fun bindCrashReporter(impl: TimberCrashReporter): CrashReporter
}
