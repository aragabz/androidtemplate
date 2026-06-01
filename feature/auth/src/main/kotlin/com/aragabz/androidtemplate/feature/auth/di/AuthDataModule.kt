package com.aragabz.androidtemplate.feature.auth.di

import com.aragabz.androidtemplate.feature.auth.data.repository.AuthRepositoryImpl
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data layer bindings for Auth feature.
 */
@Module
@InstallIn(SingletonComponent::class)
public interface AuthDataModule {
    
    /**
     * Binds [AuthRepositoryImpl] to [AuthRepository] interface.
     */
    @Binds
    @Singleton
    public fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
