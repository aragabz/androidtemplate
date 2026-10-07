package com.aragabz.androidtemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.feature.auth.ui.presentation.navigation.AuthRoute
import com.aragabz.androidtemplate.feature.auth.ui.presentation.navigation.authScreen
import com.aragabz.androidtemplate.feature.home.presentation.navigation.homeGraph
import com.aragabz.androidtemplate.feature.settings.ui.presentation.navigation.settingsScreen
import com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation.todosScreen
import kotlinx.serialization.Serializable

/** The bottom-nav shell ([MainScreen]). */
@Serializable
data object MainRoute

/**
 * App navigation graph: composes each feature's graph and owns the top-level routing between
 * sign-in ([AuthRoute]) and the shell ([MainRoute]).
 *
 * @param startDestination [AuthRoute] or [MainRoute], decided by MainViewModel from the stored session.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Any,
    onSignOut: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        authScreen(
            onSignedIn = {
                navController.navigate(MainRoute) {
                    popUpTo<AuthRoute> { inclusive = true }
                }
            },
        )

        composable<MainRoute> {
            MainScreen(navController = navController, onSignOut = onSignOut)
        }

        // Screens opened from the tabs, pushed above the shell.
        homeGraph(navController = navController)
        todosScreen(navController = navController)
        settingsScreen(navController = navController)
    }
}

/**
 * Clears the back stack and shows sign-in, e.g. after sign-out or a 401.
 */
fun NavHostController.navigateToSignIn() {
    navigate(AuthRoute) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
