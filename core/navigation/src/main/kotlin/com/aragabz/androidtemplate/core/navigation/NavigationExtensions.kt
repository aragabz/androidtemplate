package com.aragabz.androidtemplate.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Navigate to Home screen
 */
fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(Route.Home, builder)
}

/**
 * Navigate to Details screen with item ID
 */
fun NavController.navigateToDetails(
    id: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(Route.Details(id), builder)
}

/**
 * Pop back stack with optional destination
 */
fun NavController.popBackStackSafely(
    route: Any? = null,
    inclusive: Boolean = false,
): Boolean =
    if (route != null) {
        popBackStack(route, inclusive)
    } else {
        popBackStack()
    }
