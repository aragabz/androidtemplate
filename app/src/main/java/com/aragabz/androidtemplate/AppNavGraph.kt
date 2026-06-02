package com.aragabz.androidtemplate

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.auth.presentation.navigation.authNavGraph
import com.aragabz.androidtemplate.feature.todos.presentation.navigation.todosScreen
import com.aragabz.androidtemplate.feature.user.presentation.navigation.profileScreen
import com.aragabz.androidtemplate.presentation.main.MainScreen
import com.aragabz.androidtemplate.presentation.splash.SplashScreen

/**
 * Main app navigation graph.
 *
 * Flow: Splash → (Register | Login | Main)
 * Splash checks local accounts DB to decide the initial destination.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash,
    ) {
        // Splash screen — entry point, determines auth state
        composable<Route.Splash> {
            SplashScreen(navController = navController)
        }

        // Auth feature routes (Login, Register)
        authNavGraph(navController = navController)

        // Main screen with bottom navigation (Home, Todos, Settings)
        composable<Route.Main> {
            MainScreen(navController = navController)
        }

        // Todos sub-routes (AddTodo, TodoDetails) — navigated from within MainScreen's Todos tab
        todosScreen(navController = navController)

        // User feature routes (Profile, EditProfile)
        profileScreen(navController = navController)
    }
}
