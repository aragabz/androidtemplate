package com.aragabz.androidtemplate.feature.profile.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import com.aragabz.androidtemplate.feature.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val getProfileUseCase: GetProfileUseCase,
        private val signOutUseCase: SignOutUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ProfileUiState())
        val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

        init {
            refreshProfile()
        }

        fun onEvent(event: ProfileEvent) {
            when (event) {
                ProfileEvent.OnRefresh -> refreshProfile()
                ProfileEvent.OnSignOut -> signOut()
                ProfileEvent.OnDismissError -> _uiState.update { it.copy(error = null) }
            }
        }

        private fun refreshProfile() {
            viewModelScope.launch {
                getProfileUseCase().collect { result ->
                    when (result) {
                        is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AppResult.Success -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                profile = result.data,
                                error = null,
                            )
                        }
                        is AppResult.Error -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.errorUiText,
                            )
                        }
                    }
                }
            }
        }

        private fun signOut() {
            viewModelScope.launch {
                signOutUseCase().collect { result ->
                    when (result) {
                        is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AppResult.Success -> _uiState.update {
                            it.copy(isLoading = false, profile = null, error = null)
                        }
                        is AppResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.errorUiText)
                        }
                    }
                }
            }
        }
    }
