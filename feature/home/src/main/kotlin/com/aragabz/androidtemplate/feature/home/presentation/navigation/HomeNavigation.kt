package com.aragabz.androidtemplate.feature.home.presentation.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.ErrorScreen
import com.aragabz.androidtemplate.feature.home.R
import com.aragabz.androidtemplate.feature.home.presentation.main.MainScreen

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable<Route.Auth> {
        com.aragabz.androidtemplate.feature.auth.ui.presentation.AuthScreen(
            navController = navController
        )
    }

    composable<Route.Main> {
        MainScreen(navController = navController)
    }

    composable<Route.Empty> {
        EmptyScreen(
            message = stringResource(R.string.home_empty_screen_message),
            subtitle = stringResource(R.string.home_empty_screen_subtitle),
            actionLabel = stringResource(R.string.home_empty_screen_action),
            onAction = { navController.popBackStack() },
        )
    }

    composable<Route.Error> {
        ErrorScreen(
            message = stringResource(R.string.home_error_message),
            onRetry = { navController.popBackStack() },
        )
    }
}
