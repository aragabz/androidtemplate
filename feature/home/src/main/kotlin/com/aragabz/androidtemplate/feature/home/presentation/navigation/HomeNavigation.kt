package com.aragabz.androidtemplate.feature.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.home.presentation.main.MainScreen
import com.aragabz.androidtemplate.feature.home.presentation.splash.SplashScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable<Route.Splash> {
        SplashScreen(navController = navController)
    }

    composable<Route.Main> {
        MainScreen(navController = navController as androidx.navigation.NavHostController)
    }
}
