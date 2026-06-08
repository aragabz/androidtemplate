package com.aragabz.androidtemplate.core.designsystem.util

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Represents the type of device the app is running on.
 */
enum class DeviceType {
    Phone,
    Tablet,
    Foldable
}

/**
 * A helper function to determine the [DeviceType] based on the current [WindowWidthSizeClass].
 */
@Composable
fun rememberDeviceType(): DeviceType {
    val windowSizeClass = rememberWindowSizeClass()
    
    return remember(windowSizeClass) {
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> DeviceType.Phone
            WindowWidthSizeClass.Medium -> DeviceType.Foldable
            WindowWidthSizeClass.Expanded -> DeviceType.Tablet
            else -> DeviceType.Phone
        }
    }
}
