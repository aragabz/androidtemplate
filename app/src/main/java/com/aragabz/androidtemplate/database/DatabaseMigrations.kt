package com.aragabz.androidtemplate.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migrations of [AppDatabase]. Bump [CURRENT_VERSION] and add the new migration to [ALL] together.
 */
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

                // Copy data, converting String dates to Long epoch millis:
                // digits-only strings are already millis, ISO 8601 strings are parsed, anything else becomes "now".
                database.execSQL(
                    """
                    INSERT INTO todos_new (id, userId, title, description, isCompleted, createdAt, updatedAt)
                    SELECT 
                        id,
                        userId,
                        title,
                        description,
                        isCompleted,
                        ${toEpochMillis("createdAt")} AS createdAt,
                        CASE WHEN updatedAt IS NULL THEN NULL ELSE ${toEpochMillis("updatedAt")} END AS updatedAt
                    FROM todos
                    """.trimIndent(),
                )

                // Drop old table and rename new one
                database.execSQL("DROP TABLE todos")
                database.execSQL("ALTER TABLE todos_new RENAME TO todos")
            }
        }

    val ALL = arrayOf(MIGRATION_3_4, MIGRATION_4_5)

    private fun toEpochMillis(column: String): String =
        """
        CASE
            WHEN $column <> '' AND $column NOT GLOB '*[^0-9]*' THEN CAST($column AS INTEGER)
            WHEN strftime('%s', $column) IS NOT NULL THEN CAST(strftime('%s', $column) AS INTEGER) * 1000
            ELSE CAST(strftime('%s', 'now') AS INTEGER) * 1000
        END
        """.trimIndent()

    fun from(version: Int): Array<Migration> =
        ALL
            .filter { it.startVersion >= version }
            .sortedBy { it.startVersion }
            .toTypedArray()
}
