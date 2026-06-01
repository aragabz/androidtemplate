package com.aragabz.androidtemplate.feature.user.presentation.editprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.user.domain.usecase.GetProfileUseCase
import com.aragabz.androidtemplate.feature.user.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the edit profile screen.
 * Handles form validation, profile updates, and state management.
 *
 * @property getProfileUseCase Use case to fetch current profile
 * @property updateProfileUseCase Use case to update profile
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    /**
     * Handle UI events from the edit profile screen.
     *
     * @param event The event to handle
     */
    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnNameChanged -> {
                _uiState.update { 
                    it.copy(
                        name = event.name,
                        nameError = validateName(event.name)
                    )
                }
            }
            is EditProfileEvent.OnEmailChanged -> {
                _uiState.update { 
                    it.copy(
                        email = event.email,
                        emailError = validateEmail(event.email)
                    )
                }
            }
            is EditProfileEvent.OnBioChanged -> {
                _uiState.update { 
                    it.copy(
                        bio = event.bio,
                        bioError = validateBio(event.bio)
                    )
                }
            }
            is EditProfileEvent.OnSaveClicked -> {
                if (_uiState.value.isValid) {
                    saveProfile()
                }
            }
            is EditProfileEvent.OnCancelClicked -> {
                // Navigation handled in UI layer
            }
            is EditProfileEvent.OnDismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }
    
    /**
     * Load the current profile to edit.
     */
    private fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        _uiState.update { EditProfileUiState.fromProfile(result.data) }
                    }
                    is AppResult.Error -> {
                        _uiState.update { 
                            it.copy(error = result.message ?: "Failed to load profile")
                        }
                    }
                    is AppResult.Loading -> {
                        // Initial loading handled by Profile screen
                    }
                }
            }
        }
    }
    
    /**
     * Save the updated profile.
     */
    private fun saveProfile() {
        viewModelScope.launch {
            val state = _uiState.value
            
            updateProfileUseCase(
                name = state.name.trim(),
                bio = state.bio.trim().takeIf { it.isNotBlank() }
            ).collect { result ->
                when (result) {
                    is AppResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is AppResult.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSaved = true,
                                error = null
                            )
                        }
                    }
                    is AppResult.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Failed to update profile"
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Validate name input.
     */
    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            name.length > 50 -> "Name must be less than 50 characters"
            else -> null
        }
    }
    
    /**
     * Validate email input.
     */
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                "Invalid email address"
            else -> null
        }
    }
    
    /**
     * Validate bio input.
     */
    private fun validateBio(bio: String): String? {
        return when {
            bio.length > 500 -> "Bio must be less than 500 characters"
            else -> null
        }
    }
}
