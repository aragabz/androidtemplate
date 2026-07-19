package com.aragabz.androidtemplate.core.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationsAndroidTest {
    @Test
    fun registry_includesAccountsCleanupMigration() {
        assertEquals(3, DatabaseMigrations.MIGRATION_3_4.startVersion)
        assertEquals(4, DatabaseMigrations.MIGRATION_3_4.endVersion)
    }

    @Test
    fun from_returnsExpectedUpgradePath() {
        val migrations = DatabaseMigrations.from(3)

        assertEquals(1, migrations.size)
        assertEquals(3, migrations.first().startVersion)
        assertEquals(DatabaseMigrations.CURRENT_VERSION, migrations.first().endVersion)
    }
}
