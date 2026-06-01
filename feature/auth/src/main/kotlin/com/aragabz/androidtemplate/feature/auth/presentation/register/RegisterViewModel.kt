package com.aragabz.androidtemplate.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for register screen.
 * 
 * @property registerUseCase Use case for registration operation
 */
@HiltViewModel
public class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    public val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    /**
     * Handles register screen events.
     */
    public fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged -> {
                _uiState.update { it.copy(name = event.name, errorMessage = null) }
            }
            is RegisterEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = event.confirmPassword, errorMessage = null) }
            }
            is RegisterEvent.RegisterClicked -> {
                register()
            }
            is RegisterEvent.SignInClicked -> {
                // Navigation handled in UI layer
            }
            is RegisterEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
    
    private fun register() {
        val currentState = _uiState.value
        
        // Validation
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name is required") }
            return
        }
        if (currentState.name.length < 2) {
            _uiState.update { it.copy(errorMessage = "Name must be at least 2 characters") }
            return
        }
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email is required") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(errorMessage = "Invalid email format") }
            return
        }
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password is required") }
            return
        }
        if (currentState.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (currentState.password != currentState.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }
        
        registerUseCase(currentState.name, currentState.email, currentState.password)
            .onEach { result ->
                when (result) {
                    is AppResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistrationSuccessful = true,
                                errorMessage = null
                            )
                        }
                    }
                    is AppResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Registration failed"
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
