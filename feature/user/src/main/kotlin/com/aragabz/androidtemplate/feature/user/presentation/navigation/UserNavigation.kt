package com.aragabz.androidtemplate.feature.user.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.user.presentation.editprofile.EditProfileScreen
import com.aragabz.androidtemplate.feature.user.presentation.profile.ProfileScreen

/**
 * Navigation routes for the User feature.
 */

/**
 * Add profile screen to the navigation graph.
 *
 * @param navController Navigation controller for navigation actions
 */
fun NavGraphBuilder.profileScreen(
    navController: NavController
) {
    composable<Route.Profile> {
        ProfileScreen(
            onEditProfile = {
                navController.navigate(Route.EditProfile)
            },
            onLogout = {
                // Navigation to login is handled by MainActivity's SessionManager observer
                // Just pop the entire backstack
                navController.navigate(Route.Login) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
    
    composable<Route.EditProfile> {
        EditProfileScreen(
            onSaveSuccess = {
                // Navigate back to profile after saving
                navController.popBackStack()
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}

/**
 * Navigate to the profile screen.
 */
fun NavController.navigateToProfile() {
    navigate(Route.Profile)
}

/**
 * Navigate to the edit profile screen.
 */
fun NavController.navigateToEditProfile() {
    navigate(Route.EditProfile)
}
