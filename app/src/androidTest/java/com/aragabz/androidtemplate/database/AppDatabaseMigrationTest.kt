package com.aragabz.androidtemplate.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.aragabz.androidtemplate.core.database.DatabaseMigrations
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppDatabaseMigrationTest {
    @get:Rule
    val migrationHelper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory(),
        )

    @Test
    fun migrate3To4_dropsLegacyAccountsTableAndKeepsTodos() {
        migrationHelper.createDatabase(TEST_DB, 3).apply {
            execSQL(
                "INSERT INTO todos (id, userId, title, description, isCompleted, createdAt, updatedAt) VALUES ('1', 'user-1', 'Todo', NULL, 0, '2024-06-01T00:00:00Z', NULL)",
            )
            execSQL(
                "INSERT INTO accounts (id, name, email, passwordHash, avatarUrl, bio) VALUES ('a1', 'User', 'user@example.com', 'hash', NULL, NULL)",
            )
            close()
        }

        val migratedDb =
            migrationHelper.runMigrationsAndValidate(
                TEST_DB,
                4,
                true,
                DatabaseMigrations.MIGRATION_3_4,
            )

        migratedDb.query("SELECT name FROM sqlite_master WHERE type='table' AND name='accounts'").use { cursor ->
            assertFalse(cursor.moveToFirst())
        }
        migratedDb.query("SELECT COUNT(*) FROM todos").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.getInt(0) > 0)
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
