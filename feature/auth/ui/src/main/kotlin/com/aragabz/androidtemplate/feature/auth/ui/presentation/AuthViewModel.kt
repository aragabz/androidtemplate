package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.auth.BiometricAuthManager
import com.aragabz.androidtemplate.core.common.auth.BiometricAuthResult
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignInUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import com.aragabz.androidtemplate.feature.auth.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.MessageDigest
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
        private val biometricAuthManager: BiometricAuthManager,
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        private var lastSuccessfulEmail: String? = null
        private var lastSuccessfulPassword: String? = null

        init {
            observeSession()
            observeUserPreferences()
            observeBiometricResults()
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
                AuthEvent.OnSignOut -> signOut()
                AuthEvent.OnDismissError -> _uiState.update { it.copy(error = null) }
                AuthEvent.OnBiometricAuthRequested -> _uiState.update {
                    it.copy(showBiometricPrompt = true)
                }
                is AuthEvent.OnBiometricAuthResult -> handleBiometricResult(event.success)
                is AuthEvent.OnBiometricToggled -> toggleBiometric(event.enabled)
            }
        }

        fun setBiometricAvailability(available: Boolean) {
            _uiState.update { it.copy(biometricAuthAvailable = available) }
        }

        fun onBiometricPromptShown() {
            _uiState.update { it.copy(showBiometricPrompt = false) }
        }

        private fun observeSession() {
            viewModelScope.launch {
                getAuthSessionUseCase().collect { session ->
                    _uiState.update {
                        it.copy(
                            isAuthenticated = session.isAuthenticated,
                            currentUserEmail = session.userId,
                            email = if (session.isAuthenticated) "" else it.email,
                            password = if (session.isAuthenticated) "" else it.password,
                        )
                    }
                }
            }
        }

        private fun observeUserPreferences() {
            viewModelScope.launch {
                userPreferencesRepository.userPreferences.collect { prefs ->
                    _uiState.update {
                        it.copy(biometricAuthEnabled = prefs.biometricAuthEnabled)
                    }
                    if (prefs.userId != null) {
                        lastSuccessfulEmail = prefs.userId
                    }
                }
            }
        }

        private fun observeBiometricResults() {
            viewModelScope.launch {
                biometricAuthManager.authResults.collect { result ->
                    when (result) {
                        is BiometricAuthResult.Success -> {
                            // Biometric succeeded, sign in with saved credentials
                            lastSuccessfulEmail?.let { email ->
                                lastSuccessfulPassword?.let { password ->
                                    signInWithCredentials(email, password)
                                }
                            }
                        }
                        is BiometricAuthResult.Failed -> {
                            _uiState.update {
                                it.copy(
                                    error = UiText.StringResource(R.string.auth_error_biometric_failed),
                                )
                            }
                        }
                        is BiometricAuthResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    error = UiText.DynamicString(result.errorMessage),
                                )
                            }
                        }
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

        private fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        private fun signIn() {
            val email = _uiState.value.email.trim()
            val password = _uiState.value.password
            signInWithCredentials(email, password)
        }

        private fun signInWithCredentials(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                signInUseCase(
                    SignInUseCase.Params(
                        email = email,
                        token = generateSecureToken(email, password),
                    ),
                ).collect { result ->
                    when (result) {
                        is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AppResult.Success -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = null, password = "")
                            }
                            // Save credentials for biometric use
                            lastSuccessfulEmail = email
                            lastSuccessfulPassword = password
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

        private fun signOut() {
            viewModelScope.launch {
                signOutUseCase().collect { result ->
                    when (result) {
                        is AppResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is AppResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    email = "",
                                    password = "",
                                    currentUserEmail = null,
                                )
                            }
                            lastSuccessfulEmail = null
                            lastSuccessfulPassword = null
                        }
                        is AppResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.errorUiText)
                        }
                    }
                }
            }
        }

        private fun handleBiometricResult(success: Boolean) {
            if (success) {
                // Already handled in observeBiometricResults
            } else {
                _uiState.update {
                    it.copy(error = UiText.StringResource(R.string.auth_error_biometric_failed))
                }
            }
        }

        private fun toggleBiometric(enabled: Boolean) {
            viewModelScope.launch {
                userPreferencesRepository.updateBiometricAuthEnabled(enabled)
            }
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
    }
