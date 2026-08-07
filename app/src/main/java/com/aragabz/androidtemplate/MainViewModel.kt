package com.aragabz.androidtemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.network.session.SessionManager
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main view model for app-level state.
 * Handles 401 unauthorized events from SessionManager with proper lifecycle scoping.
 */
@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val sessionManager: SessionManager,
        private val signOutUseCase: SignOutUseCase,
    ) : ViewModel() {
        private val _navigateToLogin = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

        /**
         * SharedFlow that emits when the app should navigate to login screen.
         * Collect this in your UI with lifecycle awareness.
         */
        val navigateToLogin: SharedFlow<Unit> = _navigateToLogin.asSharedFlow()

        init {
            // Observe 401 unauthorized events (viewModelScope = lifecycle-scoped)
            viewModelScope.launch {
                sessionManager.onUnauthorized.collect {
                    handleUnauthorized()
                }
            }
        }

        /**
         * Handles 401 unauthorized responses by:
         * 1. Signing out the user (clears local session)
         * 2. Notifying UI to navigate to login screen
         */
        private fun handleUnauthorized() {
            viewModelScope.launch {
                // Clear local session
                signOutUseCase()
                // Notify UI to navigate to login
                _navigateToLogin.tryEmit(Unit)
            }
        }
    }
