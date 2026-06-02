package com.aragabz.androidtemplate.core.database.di

import android.content.Context
import androidx.room.Room
import com.aragabz.androidtemplate.core.database.AppDatabase
import com.aragabz.androidtemplate.core.database.dao.AccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()
}
