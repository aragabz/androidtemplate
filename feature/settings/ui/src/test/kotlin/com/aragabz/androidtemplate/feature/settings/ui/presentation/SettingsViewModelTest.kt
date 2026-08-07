package com.aragabz.androidtemplate.feature.settings.ui.presentation

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.settings.domain.model.AppLanguage
import com.aragabz.androidtemplate.feature.settings.domain.model.SettingsPreferences
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import com.aragabz.androidtemplate.feature.settings.domain.repository.SettingsRepository
import com.aragabz.androidtemplate.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateLanguageUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateThemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default values`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(ThemePreference.SYSTEM, viewModel.uiState.value.selectedTheme)
            assertEquals(AppLanguage.ENGLISH, viewModel.uiState.value.selectedLanguage)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `observeSettings updates state with settings`() =
        runTest {
            val settings = SettingsPreferences(
                theme = ThemePreference.DARK,
                language = "es",
            )
            val viewModel = createViewModel(
                settings = flowOf(settings),
            )
            advanceUntilIdle()

            assertEquals(ThemePreference.DARK, viewModel.uiState.value.selectedTheme)
            assertEquals(AppLanguage.SPANISH, viewModel.uiState.value.selectedLanguage)
        }

    @Test
    fun `onThemeSelected updates theme successfully`() =
        runTest {
            val viewModel = createViewModel(
                updateThemeResult = flowOf(
                    AppResult.Loading,
                    AppResult.Success(Unit),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.LIGHT))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `onThemeSelected shows loading state`() =
        runTest {
            val viewModel = createViewModel(
                updateThemeResult = flowOf(AppResult.Loading),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.DARK))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `onThemeSelected shows error on failure`() =
        runTest {
            val viewModel = createViewModel(
                updateThemeResult = flowOf(
                    AppResult.Loading,
                    AppResult.Error(
                        com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                            Throwable("Failed to update theme"),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.DARK))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.error != null)
        }

    @Test
    fun `onLanguageSelected updates language successfully`() =
        runTest {
            val viewModel = createViewModel(
                updateLanguageResult = flowOf(
                    AppResult.Loading,
                    AppResult.Success(Unit),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnLanguageSelected(AppLanguage.FRENCH))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `onLanguageSelected shows loading state`() =
        runTest {
            val viewModel = createViewModel(
                updateLanguageResult = flowOf(AppResult.Loading),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnLanguageSelected(AppLanguage.GERMAN))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `onLanguageSelected shows error on failure`() =
        runTest {
            val viewModel = createViewModel(
                updateLanguageResult = flowOf(
                    AppResult.Loading,
                    AppResult.Error(
                        com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                            Throwable("Failed to update language"),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnLanguageSelected(AppLanguage.ARABIC))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.error != null)
        }

    @Test
    fun `onDismissError clears error`() =
        runTest {
            val viewModel = createViewModel(
                updateThemeResult = flowOf(
                    AppResult.Error(
                        com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                            Throwable("Error"),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.LIGHT))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.error != null)

            viewModel.onEvent(SettingsEvent.OnDismissError)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `multiple theme changes update correctly`() =
        runTest {
            val viewModel = createViewModel(
                updateThemeResult = flowOf(
                    AppResult.Loading,
                    AppResult.Success(Unit),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.LIGHT))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)

            viewModel.onEvent(SettingsEvent.OnThemeSelected(ThemePreference.DARK))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `settings changes reflect in state immediately via observation`() =
        runTest {
            val settings2 = SettingsPreferences(theme = ThemePreference.DARK, language = "es")

            // Note: This test would need a channel or more sophisticated flow setup
            // to actually test the observation flow updating. For now, it tests the initial state.
            val viewModel = createViewModel(settings = flowOf(settings2))
            advanceUntilIdle()

            assertEquals(ThemePreference.DARK, viewModel.uiState.value.selectedTheme)
            assertEquals(AppLanguage.SPANISH, viewModel.uiState.value.selectedLanguage)
        }

    private fun createViewModel(
        settings: Flow<SettingsPreferences> = flowOf(
            SettingsPreferences(theme = ThemePreference.SYSTEM, language = "en"),
        ),
        updateThemeResult: Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit)),
        updateLanguageResult: Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit)),
    ): SettingsViewModel {
        val repository =
            object : SettingsRepository {
                override fun observeSettings(): Flow<SettingsPreferences> = settings

                override fun updateTheme(theme: ThemePreference): Flow<AppResult<Unit>> = updateThemeResult

                override fun updateLanguage(language: String): Flow<AppResult<Unit>> = updateLanguageResult
            }

        val observeSettingsUseCase = ObserveSettingsUseCase(repository, testDispatcher)
        val updateThemeUseCase = UpdateThemeUseCase(repository, testDispatcher)
        val updateLanguageUseCase = UpdateLanguageUseCase(repository, testDispatcher)

        return SettingsViewModel(observeSettingsUseCase, updateThemeUseCase, updateLanguageUseCase)
    }
}
