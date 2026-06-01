package com.aragabz.androidtemplate.feature.user.di

import com.aragabz.androidtemplate.feature.user.data.repository.UserRepositoryImpl
import com.aragabz.androidtemplate.feature.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data layer bindings for User feature.
 */
@Module
@InstallIn(SingletonComponent::class)
public interface UserDataModule {
    
    @Binds
    @Singleton
    public fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
