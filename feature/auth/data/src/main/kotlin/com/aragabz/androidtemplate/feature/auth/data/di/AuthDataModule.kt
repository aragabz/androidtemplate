package com.aragabz.androidtemplate.feature.auth.data.di

import com.aragabz.androidtemplate.feature.auth.data.repository.AuthRepositoryImpl
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AuthDataModule {
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
