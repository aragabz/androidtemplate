package com.aragabz.androidtemplate.feature.profile.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.core.ui.text.errorUiText
import com.aragabz.androidtemplate.feature.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Profile state is one collection of the profile stream; [ProfileEvent.OnRefresh] restarts it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        getProfileUseCase: GetProfileUseCase,
    ) : ViewModel() {
        private val refreshRequests = MutableStateFlow(0)
        private val error = MutableStateFlow<UiText?>(null)

        private val profileState =
            refreshRequests
                .flatMapLatest { getProfileUseCase() }
                .runningFold(ProfileUiState(isLoading = true)) { state, result ->
                    when (result) {
                        is AppResult.Loading -> state.copy(isLoading = true)
                        is AppResult.Success -> state.copy(isLoading = false, profile = result.data)
                        is AppResult.Error -> {
                            error.value = result.errorUiText
                            state.copy(isLoading = false)
                        }
                    }
                }

        val uiState: StateFlow<ProfileUiState> =
            combine(profileState, error) { state, error -> state.copy(error = error) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = ProfileUiState(isLoading = true),
                )

        fun onEvent(event: ProfileEvent) {
            when (event) {
                ProfileEvent.OnRefresh -> {
                    error.value = null
                    refreshRequests.update { it + 1 }
                }
                ProfileEvent.OnDismissError -> error.value = null
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
