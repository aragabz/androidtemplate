package com.aragabz.androidtemplate.feature.user.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.network.NetworkMonitor
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.user.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the profile screen.
 * Manages profile data loading, refresh, and logout functionality.
 *
 * @property getProfileUseCase Use case to fetch user profile
 * @property userPreferencesRepository Repository for clearing session on logout
 * @property networkMonitor Network connectivity monitor
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
        observeNetworkStatus()
    }
    
    /**
     * Handle UI events from the profile screen.
     *
     * @param event The event to handle
     */
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnRefresh -> loadProfile(forceRefresh = true)
            is ProfileEvent.OnEditClicked -> {
                // Navigation handled in the UI layer
            }
            is ProfileEvent.OnLogoutClicked -> logout()
            is ProfileEvent.OnRetry -> loadProfile(forceRefresh = true)
            is ProfileEvent.OnDismissError -> {
                _uiState.update { it.copy(error = null) }
            }
            is ProfileEvent.OnDismissUpdateSuccess -> {
                _uiState.update { it.copy(updateSuccess = false) }
            }
        }
    }
    
    /**
     * Load the user profile.
     *
     * @param forceRefresh Whether to force a refresh from the network (currently not used)
     */
    private fun loadProfile(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // Note: forceRefresh is ignored for now, can be implemented in repository later
            getProfileUseCase().collect { result ->
                when (result) {
                    is AppResult.Loading -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                    is AppResult.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                profile = result.data,
                                error = null
                            )
                        }
                    }
                    is AppResult.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Failed to load profile"
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Logout the current user by clearing session data.
     * Navigation to login is handled by MainActivity's SessionManager observer.
     */
    private fun logout() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isUpdating = true) }
                
                // Clear session data
                userPreferencesRepository.clearSession()
                
                // Clear profile from state
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        profile = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        error = e.message ?: "Failed to logout"
                    )
                }
            }
        }
    }
    
    /**
     * Observe network connectivity status.
     */
    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _uiState.update { it.copy(isOffline = !isOnline) }
            }
        }
    }
}
