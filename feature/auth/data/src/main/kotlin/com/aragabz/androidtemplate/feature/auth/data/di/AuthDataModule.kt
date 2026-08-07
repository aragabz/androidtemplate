package com.aragabz.androidtemplate.feature.auth.data.di

import com.aragabz.androidtemplate.feature.auth.data.api.AuthApi
import com.aragabz.androidtemplate.feature.auth.data.repository.AuthRepositoryImpl
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthDataModule {
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi =
            retrofit.create(AuthApi::class.java)
    }
}
