package com.aragabz.androidtemplate.core.datastore.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppThemeTest {
    @Test
    fun `fromStoredValue parses known names`() {
        AppTheme.entries.forEach { assertEquals(it, AppTheme.fromStoredValue(it.name)) }
    }

    @Test
    fun `fromStoredValue falls back to SYSTEM for unknown or missing values`() {
        assertEquals(AppTheme.SYSTEM, AppTheme.fromStoredValue("SEPIA"))
        assertEquals(AppTheme.SYSTEM, AppTheme.fromStoredValue(""))
        assertEquals(AppTheme.SYSTEM, AppTheme.fromStoredValue(null))
    }
}
