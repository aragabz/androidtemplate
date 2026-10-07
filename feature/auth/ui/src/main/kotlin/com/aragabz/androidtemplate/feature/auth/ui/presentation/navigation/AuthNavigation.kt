package com.aragabz.androidtemplate.feature.auth.ui.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.feature.auth.ui.presentation.AuthScreen
import kotlinx.serialization.Serializable

/** Sign-in screen. */
@Serializable
data object AuthRoute

/**
 * Registers the sign-in screen. Where to go after signing in is decided by the caller (the app shell).
 */
fun NavGraphBuilder.authScreen(onSignedIn: () -> Unit) {
    composable<AuthRoute> {
        AuthScreen(onSignedIn = onSignedIn)
    }
}
