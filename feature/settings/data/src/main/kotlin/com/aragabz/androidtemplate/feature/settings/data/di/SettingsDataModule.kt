package com.aragabz.androidtemplate.feature.settings.data.di

import com.aragabz.androidtemplate.feature.settings.data.repository.SettingsRepositoryImpl
import com.aragabz.androidtemplate.feature.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SettingsDataModule {
    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
