package com.aragabz.androidtemplate.feature.settings.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.SecureSessionStorageImpl
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepositoryImpl
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsRepositoryImplAndroidTest {
    private lateinit var preferencesRepository: UserPreferencesRepositoryImpl
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        preferencesRepository = UserPreferencesRepositoryImpl(context, SecureSessionStorageImpl(context))
        preferencesRepository.clearSession()
        preferencesRepository.updateLanguage("en")
        repository = SettingsRepositoryImpl(preferencesRepository)
    }

    @Test
    fun updatesThemeAndLanguage_fromBackingDataStore() = runBlocking {
        val themeResults = repository.updateTheme(ThemePreference.DARK).toList()
        val languageResults = repository.updateLanguage("ar").toList()

        assertTrue(themeResults.first() is AppResult.Loading)
        assertTrue(themeResults.last() is AppResult.Success)
        assertTrue(languageResults.first() is AppResult.Loading)
        assertTrue(languageResults.last() is AppResult.Success)

        val settings = repository.observeSettings().first()
        assertEquals(ThemePreference.DARK, settings.theme)
        assertEquals("ar", settings.language)
    }
}
