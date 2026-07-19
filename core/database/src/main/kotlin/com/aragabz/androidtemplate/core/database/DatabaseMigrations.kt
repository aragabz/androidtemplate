package com.aragabz.androidtemplate.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    const val CURRENT_VERSION = 4

    val MIGRATION_3_4 =
        object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS accounts")
            }
        }

    val ALL = arrayOf(MIGRATION_3_4)

    fun from(version: Int): Array<Migration> =
        ALL.filter { it.startVersion >= version }
            .sortedBy { it.startVersion }
            .toTypedArray()
}
