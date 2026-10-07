package com.aragabz.androidtemplate.feature.home.presentation.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.ErrorScreen
import com.aragabz.androidtemplate.feature.home.R
import kotlinx.serialization.Serializable

/** Demo of the shared empty state, opened from the home tab. */
@Serializable
data object HomeEmptyDemoRoute

/** Demo of the shared error state, opened from the home tab. */
@Serializable
data object HomeErrorDemoRoute

/**
 * Registers the home feature's demo screens. The home tab itself is hosted by the app's bottom-nav shell.
 */
fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable<HomeEmptyDemoRoute> {
        EmptyScreen(
            message = stringResource(R.string.home_empty_screen_message),
            subtitle = stringResource(R.string.home_empty_screen_subtitle),
            actionLabel = stringResource(R.string.home_empty_screen_action),
            onAction = { navController.popBackStack() },
        )
    }

    composable<HomeErrorDemoRoute> {
        ErrorScreen(
            message = stringResource(R.string.home_error_message),
            onRetry = { navController.popBackStack() },
        )
    }
}

/**
 * Navigate to the empty state demo.
 */
fun NavController.navigateToEmptyDemo() {
    navigate(HomeEmptyDemoRoute)
}

/**
 * Navigate to the error state demo.
 */
fun NavController.navigateToErrorDemo() {
    navigate(HomeErrorDemoRoute)
}
