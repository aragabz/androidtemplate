package com.aragabz.androidtemplate.feature.settings.ui.presentation

import app.cash.turbine.test
import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.core.testing.awaitItemMatching
import com.aragabz.androidtemplate.feature.settings.domain.model.AppLanguage
import com.aragabz.androidtemplate.feature.settings.domain.model.SettingsPreferences
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import com.aragabz.androidtemplate.feature.settings.domain.repository.SettingsRepository
import com.aragabz.androidtemplate.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateLanguageUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateThemeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /** Stores settings in memory, or fails every write when [failure] is set. */
    private class FakeSettingsRepository : SettingsRepository {
        val settings = MutableStateFlow(SettingsPreferences())
        var failure: Throwable? = null

        override fun observeSettings(): Flow<SettingsPreferences> = settings

        override fun updateTheme(theme: ThemePreference): Flow<AppResult<Unit>> = write { copy(theme = theme) }

        override fun updateLanguage(language: String): Flow<AppResult<Unit>> = write { copy(language = language) }

        private fun write(change: SettingsPreferences.() -> SettingsPreferences): Flow<AppResult<Unit>> {
            val error = failure
            return if (error != null) {
                flowOf(AppResult.Loading, AppResult.Error(AppError.UnknownError(error)))
            } else {
                flow {
                    emit(AppResult.Loading)
                    settings.update(change)
                    emit(AppResult.Success(Unit))
                }
            }
        }
    }

    private val repository = FakeSettingsRepository()
    private val viewModel =
        mainDispatcherRule.dispatcher.let { dispatcher ->
            SettingsViewModel(
                observeSettingsUseCase = ObserveSettingsUseCase(repository, dispatcher),
                updateThemeUseCase = UpdateThemeUseCase(repository, dispatcher),
                updateLanguageUseCase = UpdateLanguageUseCase(repository, dispatcher),
            )
        }

    @Test
    fun `shows the stored settings`() =
        runTest {
            repository.settings.value = SettingsPreferences(theme = ThemePreference.DARK, language = "es")

            viewModel.uiState.test {
                skipItems(1) // initial value, before the stored settings are read
                val state = awaitItem()
                assertEquals(ThemePreference.DARK, state.selectedTheme)
                assertEquals(AppLanguage.SPANISH, state.selectedLanguage)
            }
        }

    @Test
    fun `selecting a theme stores it`() =
        runTest {
            viewModel.uiState.test {
                viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.LIGHT))

                val state = awaitItemMatching { it.selectedTheme == ThemePreference.LIGHT && !it.isLoading }
                assertNull(state.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `selecting a language stores it`() =
        runTest {
            viewModel.uiState.test {
                viewModel.onEvent(SettingsEvent.OnLanguageSelected(AppLanguage.SPANISH))

                awaitItemMatching { it.selectedLanguage == AppLanguage.SPANISH }
                assertEquals("es", repository.settings.value.language)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `failed write shows an error until it is dismissed`() =
        runTest {
            repository.failure = IllegalStateException("disk full")

            viewModel.uiState.test {
                viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.DARK))

                val failed = awaitItemMatching { it.error != null && !it.isLoading }
                assertEquals(ThemePreference.SYSTEM, failed.selectedTheme)

                viewModel.onEvent(SettingsEvent.OnDismissError)
                awaitItemMatching { it.error == null }
            }
        }
}
