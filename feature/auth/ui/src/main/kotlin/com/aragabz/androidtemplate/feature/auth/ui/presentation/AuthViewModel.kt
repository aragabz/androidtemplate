package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.core.ui.text.errorUiText
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignInUseCase
import com.aragabz.androidtemplate.feature.auth.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val getAuthSessionUseCase: GetAuthSessionUseCase,
        private val signInUseCase: SignInUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        init {
            observeSession()
        }

        fun onEvent(event: AuthEvent) {
            when (event) {
                is AuthEvent.OnEmailChanged -> _uiState.update {
                    it.copy(email = event.value, emailError = null)
                }
                is AuthEvent.OnPasswordChanged -> _uiState.update {
                    it.copy(password = event.value, passwordError = null)
                }
                AuthEvent.OnToggleMode -> _uiState.update {
                    it.copy(
                        isSignUpMode = !it.isSignUpMode,
                        emailError = null,
                        passwordError = null,
                        error = null,
                    )
                }
                AuthEvent.OnSubmit -> handleSubmit()
                AuthEvent.OnDismissError -> _uiState.update { it.copy(error = null) }
            }
        }

        private fun observeSession() {
            viewModelScope.launch {
                getAuthSessionUseCase().collect { session ->
                    _uiState.update {
                        it.copy(
                            isAuthenticated = session.isAuthenticated,
                            email = if (session.isAuthenticated) "" else it.email,
                            password = if (session.isAuthenticated) "" else it.password,
                        )
                    }
                }
            }
        }

        private fun handleSubmit() {
            if (!validateInputs()) {
                return
            }

            if (_uiState.value.isSignUpMode) {
                signUp()
            } else {
                signIn()
            }
        }

        private fun validateInputs(): Boolean {
            val email = _uiState.value.email.trim()
            val password = _uiState.value.password

            var isValid = true

            if (!isValidEmail(email)) {
                _uiState.update {
                    it.copy(emailError = UiText.StringResource(R.string.auth_error_invalid_email))
                }
                isValid = false
            }

            if (password.length < 6) {
                _uiState.update {
                    it.copy(passwordError = UiText.StringResource(R.string.auth_error_password_too_short))
                }
                isValid = false
            }

            return isValid
        }

        private fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email)

        private fun signIn() {
            val email = _uiState.value.email.trim()
            val password = _uiState.value.password
            viewModelScope.launch {
                signInUseCase(
                    SignInUseCase.Params(
                        email = email,
                        token = generateSecureToken(email, password),
                    ),
                ).collect { result ->
                    when (result) {
                        is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AppResult.Success -> _uiState.update {
                            it.copy(isLoading = false, error = null, password = "")
                        }
                        is AppResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.errorUiText)
                        }
                    }
                }
            }
        }

        private fun signUp() {
            // For now, sign up uses the same logic as sign in
            // In a real app, this would call a separate API endpoint
            signIn()
        }

        /**
         * Generate a secure token using SHA-256 hash.
         * In a real production app, this would be handled server-side.
         * The server would validate credentials and return a JWT or session token.
         */
        private fun generateSecureToken(
            email: String,
            password: String,
        ): String {
            val input = "$email:$password"
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return "token_" + bytes.joinToString("") { "%02x".format(it) }
        }

        private companion object {
            // Same pattern as android.util.Patterns.EMAIL_ADDRESS, which is unavailable in JVM unit tests.
            val EMAIL_REGEX =
                Regex(
                    "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+",
                )
        }
    }
