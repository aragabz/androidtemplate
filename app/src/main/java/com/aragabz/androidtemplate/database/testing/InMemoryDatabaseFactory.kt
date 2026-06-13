package com.aragabz.androidtemplate.database.testing

import android.content.Context
import androidx.room.Room
import com.aragabz.androidtemplate.database.AppDatabase

/**
 * Factory for creating in-memory [AppDatabase] for testing.
 */
object InMemoryDatabaseFactory {
    fun create(context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}
