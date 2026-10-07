package com.aragabz.androidtemplate.feature.profile.data.di

import com.aragabz.androidtemplate.feature.profile.data.api.ProfileApi
import com.aragabz.androidtemplate.feature.profile.data.repository.ProfileRepositoryImpl
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ProfileDataModule {
    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideProfileApi(retrofit: Retrofit): ProfileApi = retrofit.create(ProfileApi::class.java)
    }
}
