package com.aragabz.androidtemplate.feature.profile.ui.presentation

import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: UiText? = null,
)
