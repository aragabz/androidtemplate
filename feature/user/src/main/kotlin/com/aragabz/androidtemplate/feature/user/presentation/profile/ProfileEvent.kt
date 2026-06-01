package com.aragabz.androidtemplate.feature.user.presentation.profile

/**
 * Events that can be triggered from the profile screen.
 */
sealed class ProfileEvent {
    /**
     * Refresh the profile data.
     */
    data object OnRefresh : ProfileEvent()
    
    /**
     * Navigate to edit profile screen.
     */
    data object OnEditClicked : ProfileEvent()
    
    /**
     * Logout the current user.
     */
    data object OnLogoutClicked : ProfileEvent()
    
    /**
     * Retry loading profile after an error.
     */
    data object OnRetry : ProfileEvent()
    
    /**
     * Dismiss the error message.
     */
    data object OnDismissError : ProfileEvent()
    
    /**
     * Dismiss the update success message.
     */
    data object OnDismissUpdateSuccess : ProfileEvent()
}
