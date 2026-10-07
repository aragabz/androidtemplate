package com.aragabz.androidtemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.network.session.SessionManager
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import com.aragabz.androidtemplate.feature.auth.ui.presentation.navigation.AuthRoute
import com.aragabz.androidtemplate.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * App-level state: start destination, theme, language changes and sign-out.
 * Sign-out (from the profile tab or a 401 response) clears the session, then asks the UI to show sign-in.
 */
@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        sessionManager: SessionManager,
        getAuthSessionUseCase: GetAuthSessionUseCase,
        userPreferencesRepository: UserPreferencesRepository,
        private val signOutUseCase: SignOutUseCase,
    ) : ViewModel() {
        // Read once: the start destination must not change while the graph is shown.
        private val startDestination: Flow<Any> =
            flow {
                val isAuthenticated = getAuthSessionUseCase().first().isAuthenticated
                emit(if (isAuthenticated) MainRoute else AuthRoute)
            }

        private val isSignInRequired = MutableStateFlow(false)

        val uiState: StateFlow<MainUiState> =
            combine(
                startDestination,
                userPreferencesRepository.userPreferences.map { it.theme },
                isSignInRequired,
            ) { startDestination, theme, isSignInRequired ->
                MainUiState.Ready(
                    startDestination = startDestination,
                    theme = theme,
                    isSignInRequired = isSignInRequired,
                )
            }.stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState.Loading)

        /**
         * Languages picked in settings. The stored value is skipped, so a user who never picked a language keeps
         * the system one; Android/AppCompat persist each applied choice.
         */
        val languageChanges: Flow<String> =
            userPreferencesRepository.userPreferences
                .map { it.language }
                .distinctUntilChanged()
                .drop(1)

        init {
            viewModelScope.launch {
                sessionManager.onUnauthorized.collect { signOut() }
            }
        }

        /**
         * Clears the session, then navigates to sign-in even if clearing failed, so the user is never stuck.
         */
        fun signOut() {
            viewModelScope.launch {
                val result = signOutUseCase().first { it !is AppResult.Loading }
                if (result is AppResult.Error) {
                    Timber.w(result.exception, "Sign-out failed to clear the local session")
                }
                isSignInRequired.value = true
            }
        }

        /**
         * Called by the UI once it has shown the sign-in screen requested by [MainUiState.Ready.isSignInRequired].
         */
        fun onSignInShown() {
            isSignInRequired.value = false
        }
    }
