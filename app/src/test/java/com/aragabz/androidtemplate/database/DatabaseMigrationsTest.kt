package com.aragabz.androidtemplate.database

import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseMigrationsTest {
    @Test
    fun registry_includesAccountsCleanupMigration() {
        assertEquals(3, DatabaseMigrations.MIGRATION_3_4.startVersion)
        assertEquals(4, DatabaseMigrations.MIGRATION_3_4.endVersion)
    }

    @Test
    fun from_returnsExpectedUpgradePath() {
        val migrations = DatabaseMigrations.from(3)

        assertEquals(DatabaseMigrations.CURRENT_VERSION - 3, migrations.size)
        assertEquals(3, migrations.first().startVersion)
        assertEquals(DatabaseMigrations.CURRENT_VERSION, migrations.last().endVersion)
        migrations.toList().zipWithNext().forEach { (previous, next) ->
            assertEquals(previous.endVersion, next.startVersion)
        }
    }
}
