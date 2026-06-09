package com.aragabz.androidtemplate.core.designsystem.util

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

/**
 * A helper function to calculate the [WindowSizeClass] for the current activity.
 *
 * This function handles the boilerplate of getting the current activity and
 * calculating the window size class.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current ?: error("No Activity found")
    return calculateWindowSizeClass(activity)
}
