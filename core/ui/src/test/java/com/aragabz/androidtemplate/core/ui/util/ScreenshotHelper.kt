package com.aragabz.androidtemplate.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.robolectric.RuntimeEnvironment
import java.util.Locale

/**
 * Common configurations for screenshot tests.
 */
enum class DefaultTestDevices(
    val size: DpSize,
    val deviceName: String,
) {
    Phone(DpSize(360.dp, 640.dp), "Phone"),
    Tablet(DpSize(1280.dp, 800.dp), "Tablet"),
    Foldable(DpSize(600.dp, 900.dp), "Foldable"),
}

/**
 * A helper to capture screenshots with different configurations.
 */
fun captureAppScreenshot(
    composeTestRule: ComposeContentTestRule,
    name: String,
    device: DefaultTestDevices = DefaultTestDevices.Phone,
    locale: Locale = Locale.ENGLISH,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    setLocale(locale)

    composeTestRule.setContent {
        AppTheme(darkTheme = darkTheme) {
            content()
        }
    }

    val suffix = buildString {
        append("_")
        append(device.deviceName)
        append("_")
        append(locale.language)
        if (darkTheme) append("_dark") else append("_light")
    }

    composeTestRule.onRoot().captureRoboImage(
        filePath = "src/test/snapshots/${name}$suffix.png",
    )
}

/**
 * Set the locale for the test environment.
 */
private fun setLocale(locale: Locale) {
    Locale.setDefault(locale)
    val config = RuntimeEnvironment.getApplication().resources.configuration
    config.setLocale(locale)
    RuntimeEnvironment.getApplication().resources.updateConfiguration(
        config,
        RuntimeEnvironment.getApplication().resources.displayMetrics,
    )
}
