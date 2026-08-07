package com.aragabz.androidtemplate.core.designsystem.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme

/**
 * Demo composable showing that rememberDeviceType works in previews.
 * This would crash before the WindowSizeClass fix.
 */
@Composable
internal fun DeviceTypeDemo() {
    val deviceType = rememberDeviceType()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Device Type: $deviceType",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

/**
 * Preview demonstrating that rememberDeviceType doesn't crash in non-Activity contexts.
 * Before the fix, this would throw: "No Activity found"
 * After the fix, it works correctly by using LocalConfiguration.
 */
@Preview(name = "Device Type Demo - Phone", widthDp = 360, heightDp = 640)
@Composable
private fun DeviceTypeDemoPhonePreview() {
    AppTheme {
        DeviceTypeDemo()
    }
}

@Preview(name = "Device Type Demo - Tablet", widthDp = 840, heightDp = 1024)
@Composable
private fun DeviceTypeDemoTabletPreview() {
    AppTheme {
        DeviceTypeDemo()
    }
}
