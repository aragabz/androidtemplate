package com.aragabz.androidtemplate.feature.profile.ui.presentation

sealed interface ProfileEvent {
    data object OnRefresh : ProfileEvent

    data object OnSignOut : ProfileEvent

    data object OnDismissError : ProfileEvent
}
