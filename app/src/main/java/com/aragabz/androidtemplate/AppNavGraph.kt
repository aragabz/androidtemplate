package com.aragabz.androidtemplate

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.todos.presentation.navigation.todosScreen
import com.aragabz.androidtemplate.feature.user.presentation.navigation.profileScreen

/**
 * Main app navigation graph.
 *
 * Auth is currently disabled - app starts directly at Home (Todos screen).
 *
 * @param navController The navigation controller
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Home, // Start directly at home (Todos)
    ) {
        // Todos feature - home screen
        todosScreen(navController = navController)

        // User feature routes (Profile still accessible)
        profileScreen(navController = navController)

        // Auth routes commented out but kept for future use
        /*
        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Register> { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
         */
    }
}
