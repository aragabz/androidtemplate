package com.aragabz.androidtemplate.feature.user.presentation.editprofile

import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile

/**
 * UI state for the edit profile screen.
 *
 * @property name User's name input
 * @property email User's email input
 * @property bio User's bio input
 * @property nameError Name validation error
 * @property emailError Email validation error
 * @property bioError Bio validation error
 * @property isLoading Whether the update is in progress
 * @property isSaved Whether the profile was successfully saved
 * @property error General error message
 */
data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val bioError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || bioError != null
    
    val isValid: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && !hasErrors
    
    companion object {
        fun fromProfile(profile: UserProfile): EditProfileUiState {
            return EditProfileUiState(
                name = profile.name,
                email = profile.email,
                bio = profile.bio ?: ""
            )
        }
    }
}
