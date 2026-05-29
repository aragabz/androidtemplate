package com.aragabz.androidtemplate.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.aragabz.androidtemplate.ui.screens.DetailsScreen
import com.aragabz.androidtemplate.ui.screens.DetailsViewModel
import com.aragabz.androidtemplate.ui.screens.HomeScreen
import com.aragabz.androidtemplate.ui.screens.HomeViewModel

/**
 * Main navigation host that handles routing and screen composition.
 * This composable sets up the Navigation3 NavDisplay and maps routes to screens.
 * Uses lifecycle-viewmodel-navigation3 for proper ViewModel scoping.
 */
@Composable
fun NavigationHost(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    // Handle system back button
    BackHandler(enabled = navigationManager.canGoBack) {
        navigationManager.navigateBack()
    }

    NavDisplay(
        modifier = modifier,
        backStack = navigationManager.currentBackStack,
        onBack = { navigationManager.navigateBack() }
    ) { route ->
        when (route) {
            Route.Home -> NavEntry(
                key = Route.Home,
                content = {
                    // ViewModel is automatically scoped to this navigation entry
                    val homeViewModel: HomeViewModel = viewModel()
                    
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToDetails = { id ->
                            navigationManager.navigateTo(Route.Details(id))
                        }
                    )
                }
            )

            is Route.Details -> NavEntry(
                key = route,
                content = {
                    // ViewModel is automatically scoped to this navigation entry
                    val detailsViewModel: DetailsViewModel = viewModel()
                    
                    DetailsScreen(
                        id = route.id,
                        viewModel = detailsViewModel,
                        onBack = {
                            navigationManager.navigateBack()
                        }
                    )
                }
            )
        }
    }
}
