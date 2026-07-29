package com.aragabz.androidtemplate

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.home.presentation.navigation.homeGraph
import com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation.todosScreen

/**
 * Main app navigation graph.
 *
 * Flow: Auth | Main (determined by authentication status)
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Route = Route.Auth,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Home feature routes (Auth, Main, Details)
        homeGraph(navController = navController)

        // Todos sub-routes (AddTodo, TodoDetails) — navigated from within MainScreen's Todos tab
        todosScreen(navController = navController)
    }
}
