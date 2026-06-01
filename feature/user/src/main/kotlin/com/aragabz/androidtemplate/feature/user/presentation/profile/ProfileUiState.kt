package com.aragabz.androidtemplate.feature.user.presentation.profile

import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile

/**
 * UI state for the profile screen.
 *
 * @property isLoading Whether the profile is being loaded
 * @property profile The user profile data, null if not loaded
 * @property error Error message if profile loading failed
 * @property isOffline Whether the device is offline
 * @property isUpdating Whether the profile is being updated
 * @property updateSuccess Whether the profile update was successful
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
    val isOffline: Boolean = false,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false
)
