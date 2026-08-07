package com.aragabz.androidtemplate.feature.settings.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.settings.ui.presentation.language.LanguageSectionScreen
import com.aragabz.androidtemplate.feature.settings.ui.presentation.theme.ThemeSectionScreen

/**
 * Navigation extension for the settings feature.
 */
fun NavGraphBuilder.settingsScreen(navController: NavHostController) {
    composable<Route.SettingsTheme> {
        ThemeSectionScreen(navController = navController)
    }

    composable<Route.SettingsLanguage> {
        LanguageSectionScreen(navController = navController)
    }
}

/**
 * Navigate to theme settings section.
 */
fun NavController.navigateToThemeSettings() {
    navigate(Route.SettingsTheme)
}

/**
 * Navigate to language settings section.
 */
fun NavController.navigateToLanguageSettings() {
    navigate(Route.SettingsLanguage)
}
