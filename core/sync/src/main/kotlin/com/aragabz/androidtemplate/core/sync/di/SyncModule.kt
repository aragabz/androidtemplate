package com.aragabz.androidtemplate.core.sync.di

import com.aragabz.androidtemplate.core.sync.manager.SyncManager
import com.aragabz.androidtemplate.core.sync.manager.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SyncModule {
    @Binds
    @Singleton
    fun bindSyncManager(impl: WorkManagerSyncManager): SyncManager
}
