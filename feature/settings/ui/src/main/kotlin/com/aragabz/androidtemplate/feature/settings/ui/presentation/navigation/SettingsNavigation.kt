package com.aragabz.androidtemplate.feature.settings.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.feature.settings.ui.presentation.language.LanguageSectionScreen
import com.aragabz.androidtemplate.feature.settings.ui.presentation.theme.ThemeSectionScreen
import kotlinx.serialization.Serializable

/** Theme settings section. */
@Serializable
data object SettingsThemeRoute

/** Language settings section. */
@Serializable
data object SettingsLanguageRoute

/**
 * Registers the settings feature's section screens. The settings list itself is hosted by the app's bottom-nav shell.
 */
fun NavGraphBuilder.settingsScreen(navController: NavController) {
    composable<SettingsThemeRoute> {
        ThemeSectionScreen(onBack = { navController.popBackStack() })
    }

    composable<SettingsLanguageRoute> {
        LanguageSectionScreen(onBack = { navController.popBackStack() })
    }
}

/**
 * Navigate to theme settings section.
 */
fun NavController.navigateToThemeSettings() {
    navigate(SettingsThemeRoute)
}

/**
 * Navigate to language settings section.
 */
fun NavController.navigateToLanguageSettings() {
    navigate(SettingsLanguageRoute)
}
