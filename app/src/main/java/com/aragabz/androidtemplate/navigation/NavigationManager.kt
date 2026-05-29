package com.aragabz.androidtemplate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

/**
 * Manager class for handling navigation state and operations.
 * Provides a clean API for navigating between screens.
 */
@Stable
class NavigationManager(
    private val backStack: SnapshotStateList<Route>
) {
    /**
     * Current navigation back stack
     */
    val currentBackStack: List<Route> = backStack

    /**
     * Whether we can navigate back
     */
    val canGoBack: Boolean
        get() = backStack.size > 1

    /**
     * Navigate to a new route
     */
    fun navigateTo(route: Route) {
        backStack.add(route)
    }

    /**
     * Navigate back to the previous route
     */
    fun navigateBack() {
        if (canGoBack) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    /**
     * Navigate back to a specific route, removing all routes above it
     */
    fun navigateBackTo(route: Route) {
        val index = backStack.lastIndexOf(route)
        if (index != -1 && index < backStack.size - 1) {
            backStack.subList(index + 1, backStack.size).clear()
        }
    }

    /**
     * Replace the current route with a new one
     */
    fun replaceCurrent(route: Route) {
        if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.lastIndex)
        }
        backStack.add(route)
    }

    /**
     * Clear the back stack and navigate to a new route
     */
    fun clearAndNavigateTo(route: Route) {
        backStack.clear()
        backStack.add(route)
    }
}

/**
 * Remember a NavigationManager instance
 */
@Composable
fun rememberNavigationManager(
    initialRoute: Route = Route.Home
): NavigationManager {
    return remember {
        NavigationManager(
            backStack = listOf(initialRoute).toMutableStateList()
        )
    }
}
