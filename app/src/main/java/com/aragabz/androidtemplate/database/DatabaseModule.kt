package com.aragabz.androidtemplate.database

import android.content.Context
import androidx.room.Room
import com.aragabz.androidtemplate.BuildConfig
import com.aragabz.androidtemplate.feature.todos.data.local.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).addMigrations(*DatabaseMigrations.ALL)
            .apply {
                // Release builds must never wipe user data: a missing migration fails loudly instead.
                if (BuildConfig.DEBUG) fallbackToDestructiveMigration(dropAllTables = true)
            }.build()

    @Provides
    fun provideTodoDao(db: AppDatabase): TodoDao = db.todoDao()
}
