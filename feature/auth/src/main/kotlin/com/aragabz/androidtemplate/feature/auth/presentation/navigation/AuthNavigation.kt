package com.aragabz.androidtemplate.feature.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.auth.presentation.login.LoginScreen
import com.aragabz.androidtemplate.feature.auth.presentation.register.RegisterScreen

/**
 * Naviation extension for Auth feature screens.
 */
public fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable<Route.Login> {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate(Route.Main) {
                    popUpTo(Route.Login) { inclusive = true }
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
                navController.navigate(Route.Main) {
                    popUpTo(Route.Register) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )
    }
}

/**
 * Navigation extension to navigate to login screen.
 */
public fun NavController.navigateToLogin() {
    navigate(Route.Login) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Navigation extension to navigate to register screen.
 */
public fun NavController.navigateToRegister() {
    navigate(Route.Register)
}
