package com.aragabz.androidtemplate.feature.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.ErrorScreen
import com.aragabz.androidtemplate.feature.home.presentation.main.MainScreen
import com.aragabz.androidtemplate.feature.home.presentation.splash.SplashScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable<Route.Splash> {
        SplashScreen(navController = navController)
    }

    composable<Route.Main> {
        MainScreen(navController = navController as androidx.navigation.NavHostController)
    }

    composable<Route.Empty> {
        EmptyScreen(
            message = "This is an empty screen",
            subtitle = "You can navigate back from here",
            actionLabel = "Go Back",
            onAction = { navController.popBackStack() },
        )
    }

    composable<Route.Error> {
        ErrorScreen(
            message = "Something went wrong on this screen.",
            onRetry = { navController.popBackStack() },
        )
    }
}
