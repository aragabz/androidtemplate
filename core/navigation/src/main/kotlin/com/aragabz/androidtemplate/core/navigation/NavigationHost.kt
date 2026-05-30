package com.aragabz.androidtemplate.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

/**
 * Main navigation host for the app.
 * Features can add their own navigation graphs via extension functions.
 *
 * @param navController The navigation controller
 * @param startDestination The initial route
 * @param modifier Modifier for the NavHost
 * @param homeScreen Composable for Home screen
 * @param detailsScreen Composable for Details screen (receives id parameter)
 */
@Composable
fun AppNavigationHost(
    navController: NavHostController,
    startDestination: Route = Route.Home,
    modifier: Modifier = Modifier,
    homeScreen: @Composable (NavHostController) -> Unit,
    detailsScreen: @Composable (String, NavHostController) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home screen
        composable<Route.Home> {
            homeScreen(navController)
        }
        
        // Details screen with type-safe arguments
        composable<Route.Details> { backStackEntry ->
            val details = backStackEntry.toRoute<Route.Details>()
            detailsScreen(details.id, navController)
        }
    }
}
