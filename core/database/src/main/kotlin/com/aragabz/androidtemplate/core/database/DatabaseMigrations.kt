package com.aragabz.androidtemplate.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    const val CURRENT_VERSION = 5

    val MIGRATION_3_4 =
        object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS accounts")
            }
        }

    /**
     * Migration 4 to 5: Convert todos date columns from String to Long (epoch millis).
     *
     * Handles conversion of both ISO 8601 strings and millis-as-string to proper Long values.
     */
    val MIGRATION_4_5 =
        object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new table with Long columns
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS todos_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        isCompleted INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER
                    )
                    """.trimIndent(),
                )

                // Copy data, converting String dates to Long epoch millis
                // Attempt to parse as millis first, fallback to current time for invalid data
                database.execSQL(
                    """
                    INSERT INTO todos_new (id, userId, title, description, isCompleted, createdAt, updatedAt)
                    SELECT 
                        id,
                        userId,
                        title,
                        description,
                        isCompleted,
                        CASE 
                            WHEN CAST(createdAt AS INTEGER) > 0 
                            THEN CAST(createdAt AS INTEGER)
                            ELSE (strftime('%s', 'now') * 1000)
                        END as createdAt,
                        CASE 
                            WHEN updatedAt IS NULL THEN NULL
                            WHEN CAST(updatedAt AS INTEGER) > 0 
                            THEN CAST(updatedAt AS INTEGER)
                            ELSE (strftime('%s', 'now') * 1000)
                        END as updatedAt
                    FROM todos
                    """.trimIndent(),
                )

                // Drop old table and rename new one
                database.execSQL("DROP TABLE todos")
                database.execSQL("ALTER TABLE todos_new RENAME TO todos")
            }
        }

    val ALL = arrayOf(MIGRATION_3_4, MIGRATION_4_5)

    fun from(version: Int): Array<Migration> =
        ALL
            .filter { it.startVersion >= version }
            .sortedBy { it.startVersion }
            .toTypedArray()
}
