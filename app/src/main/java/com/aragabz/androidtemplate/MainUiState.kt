package com.aragabz.androidtemplate

import com.aragabz.androidtemplate.core.datastore.model.AppTheme

/**
 * App-level state: the native splash stays on screen while [Loading].
 */
sealed interface MainUiState {
    data object Loading : MainUiState

    /**
     * @param startDestination the first route of the nav graph, from the session stored at launch.
     * @param theme the user's stored theme, so the first frame is drawn in the right one.
     * @param isSignInRequired true after sign-out or a 401 until the UI reports it has shown sign-in.
     */
    data class Ready(
        val startDestination: Any,
        val theme: AppTheme,
        val isSignInRequired: Boolean = false,
    ) : MainUiState
}
