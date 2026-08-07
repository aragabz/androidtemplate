package com.aragabz.androidtemplate.core.designsystem.util

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * A helper function to calculate the [WindowSizeClass] for the current context.
 *
 * This function handles both Activity and non-Activity contexts (e.g., Compose previews).
 * In Activity contexts, it uses the standard [calculateWindowSizeClass].
 * In non-Activity contexts (previews, tests), it calculates based on screen dimensions.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current

    return if (activity is ComponentActivity) {
        // Activity context - use standard calculation
        calculateWindowSizeClass(activity)
    } else {
        // Non-Activity context (preview/test) - calculate from configuration
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current

        val screenWidth = with(density) { configuration.screenWidthDp.dp }
        val screenHeight = with(density) { configuration.screenHeightDp.dp }

        WindowSizeClass.calculateFromSize(DpSize(screenWidth, screenHeight))
    }
}
