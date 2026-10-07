package com.aragabz.androidtemplate.feature.settings.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.testing.FakeUserPreferencesRepository
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryImplTest {
    private val preferences = FakeUserPreferencesRepository()

    @Test
    fun `updateTheme persists the theme and emits Success`() =
        runTest {
            val repository = SettingsRepositoryImpl(preferences)

            val result = repository.updateTheme(ThemePreference.DARK).first { it !is AppResult.Loading }

            assertTrue(result is AppResult.Success)
            assertEquals(AppTheme.DARK, preferences.preferences.value.theme)
        }

    @Test
    fun `updateTheme propagates cancellation instead of emitting Error`() =
        runTest {
            val cancelling =
                object : UserPreferencesRepository by preferences {
                    override suspend fun updateTheme(theme: AppTheme): Unit = throw CancellationException("cancelled")
                }
            val repository = SettingsRepositoryImpl(cancelling)
            val emitted = mutableListOf<AppResult<Unit>>()

            val thrown = runCatching { repository.updateTheme(ThemePreference.DARK).toList(emitted) }

            assertTrue(thrown.exceptionOrNull() is CancellationException)
            assertTrue(emitted.none { it is AppResult.Error })
        }

    @Test
    fun `updateLanguage propagates cancellation instead of emitting Error`() =
        runTest {
            val cancelling =
                object : UserPreferencesRepository by preferences {
                    override suspend fun updateLanguage(language: String): Unit =
                        throw CancellationException("cancelled")
                }
            val repository = SettingsRepositoryImpl(cancelling)
            val emitted = mutableListOf<AppResult<Unit>>()

            val thrown = runCatching { repository.updateLanguage("de").toList(emitted) }

            assertTrue(thrown.exceptionOrNull() is CancellationException)
            assertTrue(emitted.none { it is AppResult.Error })
        }
}
