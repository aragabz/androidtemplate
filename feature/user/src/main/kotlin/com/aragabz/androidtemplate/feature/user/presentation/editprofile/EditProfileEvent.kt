package com.aragabz.androidtemplate.feature.user.presentation.editprofile

/**
 * Events that can be triggered from the edit profile screen.
 */
sealed class EditProfileEvent {
    /**
     * Name input changed.
     */
    data class OnNameChanged(val name: String) : EditProfileEvent()
    
    /**
     * Email input changed.
     */
    data class OnEmailChanged(val email: String) : EditProfileEvent()
    
    /**
     * Bio input changed.
     */
    data class OnBioChanged(val bio: String) : EditProfileEvent()
    
    /**
     * Save button clicked.
     */
    data object OnSaveClicked : EditProfileEvent()
    
    /**
     * Cancel button clicked.
     */
    data object OnCancelClicked : EditProfileEvent()
    
    /**
     * Dismiss error message.
     */
    data object OnDismissError : EditProfileEvent()
}
