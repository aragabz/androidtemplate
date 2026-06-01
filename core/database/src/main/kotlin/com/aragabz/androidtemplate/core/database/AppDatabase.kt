package com.aragabz.androidtemplate.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aragabz.androidtemplate.core.database.model.SampleEntity
import com.aragabz.androidtemplate.core.database.util.Converters

/**
 * Main application database.
 * Add your DAOs and entities here as you create them.
 */
@Database(
    entities = [
        SampleEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Add your DAO getters here
    // Example: abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "androidtemplate_database"
    }
}
