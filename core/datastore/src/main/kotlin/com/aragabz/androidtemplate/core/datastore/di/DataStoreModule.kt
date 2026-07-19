package com.aragabz.androidtemplate.core.datastore.di

import android.content.Context
import com.aragabz.androidtemplate.core.datastore.CachePolicyStore
import com.aragabz.androidtemplate.core.datastore.CachePolicyStoreImpl
import com.aragabz.androidtemplate.core.datastore.SecureSessionStorage
import com.aragabz.androidtemplate.core.datastore.SecureSessionStorageImpl
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing DataStore dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {
    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    abstract fun bindCachePolicyStore(impl: CachePolicyStoreImpl): CachePolicyStore

    companion object {
        @Provides
        @Singleton
        fun provideSecureSessionStorage(
            @ApplicationContext context: Context,
        ): SecureSessionStorage = SecureSessionStorageImpl(context)
    }
}
