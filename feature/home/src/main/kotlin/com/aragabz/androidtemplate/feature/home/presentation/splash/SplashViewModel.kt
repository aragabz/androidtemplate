package com.aragabz.androidtemplate.feature.home.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for managing the splash screen logic and determining the initial navigation target.
 */
@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getAuthSessionUseCase: GetAuthSessionUseCase,
    ) : ViewModel() {
        sealed interface SplashState {
            object Loading : SplashState

            object NavigateToAuth : SplashState

            object NavigateToMain : SplashState
        }

        private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
        val state: StateFlow<SplashState> = _state.asStateFlow()

        init {
            checkNavigationTarget()
        }

        private fun checkNavigationTarget() {
            viewModelScope.launch {
                // System splash covers initial startup; keep composable splash brief for handoff polish.
                delay(500.milliseconds)

                // Check if user is already authenticated
                val session = getAuthSessionUseCase().first()
                _state.value =
                    if (session.isAuthenticated) {
                        SplashState.NavigateToMain
                    } else {
                        SplashState.NavigateToAuth
                    }
            }
        }
    }
