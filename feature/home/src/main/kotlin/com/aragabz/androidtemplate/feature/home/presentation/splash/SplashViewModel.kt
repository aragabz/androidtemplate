package com.aragabz.androidtemplate.feature.home.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for managing the splash screen logic and determining the initial navigation target.
 */
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    
    sealed interface SplashState {
        object Loading : SplashState
        object NavigateToMain : SplashState
    }

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        checkNavigationTarget()
    }

    private fun checkNavigationTarget() {
        viewModelScope.launch {
            // Keep the splash screen visible for at least 1.5 seconds for branding (Vaulty)
            delay(1500.milliseconds)
            _state.value = SplashState.NavigateToMain
        }
    }
}
