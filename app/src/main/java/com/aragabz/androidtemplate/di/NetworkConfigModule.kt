package com.aragabz.androidtemplate.di

import com.aragabz.androidtemplate.BuildConfig
import com.aragabz.androidtemplate.core.datastore.SecureSessionStorage
import com.aragabz.androidtemplate.core.network.di.BaseUrl
import com.aragabz.androidtemplate.core.network.session.AuthTokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Supplies the network layer with what only the app knows: the flavor's backend and the stored session.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkConfigModule {
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    fun provideAuthTokenProvider(storage: SecureSessionStorage): AuthTokenProvider =
        AuthTokenProvider { storage.getAuthToken() }
}
