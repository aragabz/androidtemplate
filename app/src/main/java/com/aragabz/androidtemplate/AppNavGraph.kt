package com.aragabz.androidtemplate

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.home.presentation.navigation.homeGraph
import com.aragabz.androidtemplate.feature.todos.presentation.navigation.todosScreen

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
        // Home feature routes (Splash, Main, Details)
        homeGraph(navController = navController)

        // Todos sub-routes (AddTodo, TodoDetails) — navigated from within MainScreen's Todos tab
        todosScreen(navController = navController)
    }
}
