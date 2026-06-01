package com.aragabz.androidtemplate.feature.user.di

import com.aragabz.androidtemplate.feature.user.data.remote.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module providing network dependencies for User feature.
 */
@Module
@InstallIn(SingletonComponent::class)
public object UserNetworkModule {
    
    @Provides
    @Singleton
    public fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }
}
