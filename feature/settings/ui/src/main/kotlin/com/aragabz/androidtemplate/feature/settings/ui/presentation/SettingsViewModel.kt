package com.aragabz.androidtemplate.feature.settings.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.core.ui.text.errorUiText
import com.aragabz.androidtemplate.feature.settings.domain.model.AppLanguage
import com.aragabz.androidtemplate.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateLanguageUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings state derives from the stored preferences, so each settings screen can own its instance
 * and still show the same values.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        observeSettingsUseCase: ObserveSettingsUseCase,
        private val updateThemeUseCase: UpdateThemeUseCase,
        private val updateLanguageUseCase: UpdateLanguageUseCase,
    ) : ViewModel() {
        private val isLoading = MutableStateFlow(false)
        private val error = MutableStateFlow<UiText?>(null)

        val uiState: StateFlow<SettingsUiState> =
            combine(observeSettingsUseCase(), isLoading, error) { settings, isLoading, error ->
                SettingsUiState(
                    selectedTheme = settings.theme,
                    selectedLanguage = AppLanguage.fromCode(settings.language),
                    isLoading = isLoading,
                    error = error,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = SettingsUiState(),
            )

        fun onEvent(event: SettingsEvent) {
            when (event) {
                is SettingsEvent.OnThemeSelected -> update { updateThemeUseCase(event.theme) }
                is SettingsEvent.OnLanguageSelected -> update { updateLanguageUseCase(event.language.code) }
                SettingsEvent.OnDismissError -> error.value = null
            }
        }

        private fun update(mutation: () -> Flow<AppResult<Unit>>) {
            viewModelScope.launch {
                mutation().collect { result ->
                    isLoading.value = result is AppResult.Loading
                    if (result is AppResult.Error) error.value = result.errorUiText
                }
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
