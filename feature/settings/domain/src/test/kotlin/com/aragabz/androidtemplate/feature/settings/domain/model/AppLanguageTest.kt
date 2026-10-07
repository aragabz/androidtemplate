package com.aragabz.androidtemplate.feature.settings.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLanguageTest {
    @Test
    fun `all language codes are unique lowercase ISO 639-1 codes`() {
        val codes = AppLanguage.entries.map { it.code }

        assertEquals(codes.size, codes.toSet().size)
        codes.forEach { code -> assertTrue(code, code.matches(Regex("[a-z]{2}"))) }
    }

    @Test
    fun `offers only the shipped translations`() {
        assertEquals(listOf("en", "es"), AppLanguage.entries.map { it.code })
    }

    @Test
    fun `fromCode is case insensitive`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("EN"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromCode("Es"))
    }

    @Test
    fun `fromCode defaults to English for an unknown code`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("fr"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(""))
    }
}
