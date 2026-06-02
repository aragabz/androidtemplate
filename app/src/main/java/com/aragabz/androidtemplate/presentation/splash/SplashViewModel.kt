package com.aragabz.androidtemplate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.database.dao.AccountDao
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the splash screen logic and determining the initial navigation target.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    sealed interface SplashState {
        object Loading : SplashState
        object NavigateToRegister : SplashState
        object NavigateToLogin : SplashState
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
            delay(1500)
            
            val accounts = accountDao.getAccounts()
            if (accounts.isEmpty()) {
                _state.value = SplashState.NavigateToRegister
            } else {
                val prefs = preferencesRepository.userPreferences.first()
                if (!prefs.authToken.isNullOrBlank() && !prefs.userId.isNullOrBlank()) {
                    // Double check that the authenticated user actually exists locally
                    val userExists = accountDao.getAccountById(prefs.userId!!) != null
                    if (userExists) {
                        _state.value = SplashState.NavigateToMain
                    } else {
                        preferencesRepository.clearSession()
                        _state.value = SplashState.NavigateToLogin
                    }
                } else {
                    _state.value = SplashState.NavigateToLogin
                }
            }
        }
    }
}
