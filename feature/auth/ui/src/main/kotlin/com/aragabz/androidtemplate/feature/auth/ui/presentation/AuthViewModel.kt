package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignInUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val getAuthSessionUseCase: GetAuthSessionUseCase,
        private val signInUseCase: SignInUseCase,
        private val signOutUseCase: SignOutUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        init {
            observeSession()
        }

        fun onEvent(event: AuthEvent) {
            when (event) {
                is AuthEvent.OnUserIdChanged -> _uiState.update { it.copy(userIdInput = event.value) }
                AuthEvent.OnSignIn -> signIn()
                AuthEvent.OnSignOut -> signOut()
                AuthEvent.OnDismissError -> _uiState.update { it.copy(error = null) }
            }
        }

        private fun observeSession() {
            viewModelScope.launch {
                getAuthSessionUseCase().collect { session ->
                    _uiState.update {
                        it.copy(
                            isAuthenticated = session.isAuthenticated,
                            currentUserId = session.userId,
                            userIdInput = if (session.isAuthenticated) session.userId.orEmpty() else it.userIdInput,
                        )
                    }
                }
            }
        }

        private fun signIn() {
            val userId = _uiState.value.userIdInput.trim()
            if (userId.isBlank()) {
                return
            }

            viewModelScope.launch {
                signInUseCase(SignInUseCase.Params(userId = userId, token = "token_$userId")).collect { result ->
                    when (result) {
                        is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AppResult.Success -> _uiState.update { it.copy(isLoading = false, error = null) }
                        is AppResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.errorUiText)
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
                            it.copy(isLoading = false, userIdInput = "", currentUserId = null)
                        }
                        is AppResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.errorUiText)
                        }
                    }
                }
            }
        }
    }
