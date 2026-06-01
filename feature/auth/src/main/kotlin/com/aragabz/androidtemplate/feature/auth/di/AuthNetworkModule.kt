package com.aragabz.androidtemplate.feature.auth.di

import com.aragabz.androidtemplate.feature.auth.data.remote.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module providing network dependencies for Auth feature.
 */
@Module
@InstallIn(SingletonComponent::class)
public object AuthNetworkModule {
    
    /**
     * Provides [AuthApiService] instance.
     */
    @Provides
    @Singleton
    public fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
