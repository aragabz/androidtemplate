package com.aragabz.androidtemplate.feature.settings.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for AppLanguage enum.
 */
class AppLanguageTest {

    @Test
    fun `all languages have unique codes`() {
        val codes = AppLanguage.entries.map { it.code }
        val uniqueCodes = codes.toSet()
        
        assertEquals(
            "All language codes should be unique",
            codes.size,
            uniqueCodes.size
        )
    }

    @Test
    fun `all language codes are lowercase`() {
        AppLanguage.entries.forEach { language ->
            assertEquals(
                "Language code should be lowercase: ${language.code}",
                language.code,
                language.code.lowercase()
            )
        }
    }

    @Test
    fun `all language codes are 2 characters`() {
        AppLanguage.entries.forEach { language ->
            assertEquals(
                "Language code should be 2 characters: ${language.code}",
                2,
                language.code.length
            )
        }
    }

    @Test
    fun `fromCode finds correct language`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.ARABIC, AppLanguage.fromCode("ar"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromCode("es"))
        assertEquals(AppLanguage.FRENCH, AppLanguage.fromCode("fr"))
        assertEquals(AppLanguage.GERMAN, AppLanguage.fromCode("de"))
    }

    @Test
    fun `fromCode is case insensitive`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("EN"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("En"))
        assertEquals(AppLanguage.ARABIC, AppLanguage.fromCode("AR"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromCode("ES"))
    }

    @Test
    fun `fromCode defaults to English for unknown code`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("xx"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("unknown"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(""))
    }

    @Test
    fun `supportedCodes returns all language codes`() {
        val codes = AppLanguage.supportedCodes()
        
        assertEquals(AppLanguage.entries.size, codes.size)
        assertTrue(codes.contains("en"))
        assertTrue(codes.contains("ar"))
        assertTrue(codes.contains("es"))
        assertTrue(codes.contains("fr"))
        assertTrue(codes.contains("de"))
    }

    @Test
    fun `all languages have display names`() {
        AppLanguage.entries.forEach { language ->
            assertTrue(
                "Language should have non-empty display name: ${language.name}",
                language.displayName.isNotBlank()
            )
        }
    }

    @Test
    fun `English has correct values`() {
        assertEquals("en", AppLanguage.ENGLISH.code)
        assertEquals("English", AppLanguage.ENGLISH.displayName)
    }

    @Test
    fun `Arabic has correct values and native script`() {
        assertEquals("ar", AppLanguage.ARABIC.code)
        assertEquals("العربية", AppLanguage.ARABIC.displayName)
        // Verify it contains Arabic characters
        assertTrue(AppLanguage.ARABIC.displayName.any { it in '\u0600'..'\u06FF' })
    }

    @Test
    fun `Chinese has correct values and native script`() {
        assertEquals("zh", AppLanguage.CHINESE.code)
        assertEquals("中文", AppLanguage.CHINESE.displayName)
        // Verify it contains Chinese characters
        assertTrue(AppLanguage.CHINESE.displayName.any { it in '\u4E00'..'\u9FFF' })
    }

    @Test
    fun `Japanese has correct values and native script`() {
        assertEquals("ja", AppLanguage.JAPANESE.code)
        assertEquals("日本語", AppLanguage.JAPANESE.displayName)
        // Verify it contains Japanese characters
        assertTrue(
            AppLanguage.JAPANESE.displayName.any { 
                it in '\u3040'..'\u309F' || it in '\u4E00'..'\u9FFF'
            }
        )
    }

    @Test
    fun `Korean has correct values and native script`() {
        assertEquals("ko", AppLanguage.KOREAN.code)
        assertEquals("한국어", AppLanguage.KOREAN.displayName)
        // Verify it contains Korean characters
        assertTrue(AppLanguage.KOREAN.displayName.any { it in '\uAC00'..'\uD7AF' })
    }

    @Test
    fun `fromCode handles all supported languages`() {
        AppLanguage.entries.forEach { language ->
            assertEquals(
                "fromCode should return correct language for ${language.code}",
                language,
                AppLanguage.fromCode(language.code)
            )
        }
    }

    @Test
    fun `enum maintains order`() {
        val languages = AppLanguage.entries
        
        // English should be first (default)
        assertEquals(AppLanguage.ENGLISH, languages.first())
        
        // Verify we have all expected languages
        assertTrue(languages.contains(AppLanguage.ENGLISH))
        assertTrue(languages.contains(AppLanguage.ARABIC))
        assertTrue(languages.contains(AppLanguage.SPANISH))
        assertTrue(languages.contains(AppLanguage.FRENCH))
        assertTrue(languages.contains(AppLanguage.GERMAN))
        assertTrue(languages.contains(AppLanguage.ITALIAN))
        assertTrue(languages.contains(AppLanguage.PORTUGUESE))
        assertTrue(languages.contains(AppLanguage.RUSSIAN))
        assertTrue(languages.contains(AppLanguage.CHINESE))
        assertTrue(languages.contains(AppLanguage.JAPANESE))
        assertTrue(languages.contains(AppLanguage.KOREAN))
    }
}
