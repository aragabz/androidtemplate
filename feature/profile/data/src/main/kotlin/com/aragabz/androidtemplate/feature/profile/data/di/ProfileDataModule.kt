package com.aragabz.androidtemplate.feature.profile.data.di

import com.aragabz.androidtemplate.feature.profile.data.repository.ProfileRepositoryImpl
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ProfileDataModule {
    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
