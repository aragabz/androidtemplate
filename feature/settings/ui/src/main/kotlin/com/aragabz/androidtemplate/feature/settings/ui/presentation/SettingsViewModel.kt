package com.aragabz.androidtemplate.feature.settings.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.settings.domain.model.AppLanguage
import com.aragabz.androidtemplate.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateLanguageUseCase
import com.aragabz.androidtemplate.feature.settings.domain.usecase.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val observeSettingsUseCase: ObserveSettingsUseCase,
        private val updateThemeUseCase: UpdateThemeUseCase,
        private val updateLanguageUseCase: UpdateLanguageUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingsUiState())
        val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

        init {
            observeSettings()
        }

        fun onEvent(event: SettingsEvent) {
            when (event) {
                is SettingsEvent.OnThemeSelected -> updateTheme(event.theme)
                is SettingsEvent.OnLanguageSelected -> updateLanguage(event.language)
                SettingsEvent.OnDismissError -> _uiState.update { it.copy(error = null) }
            }
        }

        private fun observeSettings() {
            viewModelScope.launch {
                observeSettingsUseCase().collect { settings ->
                    _uiState.update {
                        it.copy(
                            selectedTheme = settings.theme,
                            selectedLanguage = AppLanguage.fromCode(settings.language),
                        )
                    }
                }
            }
        }

        private fun updateTheme(theme: com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference) {
            viewModelScope.launch {
                updateThemeUseCase(theme).collect { result ->
                    handleMutationResult(result)
                }
            }
        }

        private fun updateLanguage(language: AppLanguage) {
            viewModelScope.launch {
                updateLanguageUseCase(language.code).collect { result ->
                    handleMutationResult(result)
                }
            }
        }

        private fun handleMutationResult(result: AppResult<Unit>) {
            when (result) {
                is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, error = null) }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.errorUiText)
                }
            }
        }
    }
