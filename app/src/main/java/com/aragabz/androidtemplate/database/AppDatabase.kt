package com.aragabz.androidtemplate.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aragabz.androidtemplate.feature.todos.data.local.dao.TodoDao
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity

/**
 * Main application database.
 * Resides in :app module to avoid circular dependencies between feature modules.
 */
@Database(
    entities = [
        TodoEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        const val DATABASE_NAME = "androidtemplate_database"
    }
}
