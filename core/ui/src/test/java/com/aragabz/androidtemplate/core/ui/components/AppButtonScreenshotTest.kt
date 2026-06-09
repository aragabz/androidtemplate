package com.aragabz.androidtemplate.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.ui.util.DefaultTestDevices
import com.aragabz.androidtemplate.core.ui.util.captureAppScreenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w360dp-h640dp-xhdpi")
class AppButtonScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun primaryButton_light() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "AppButton_Primary",
            darkTheme = false
        ) {
            AppButton(text = "Primary Button", onClick = {})
        }
    }

    @Test
    fun primaryButton_dark() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "AppButton_Primary",
            darkTheme = true
        ) {
            AppButton(text = "Primary Button", onClick = {})
        }
    }

    @Test
    fun secondaryButton_light() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "AppButton_Secondary",
            variant = AppButtonVariant.SECONDARY,
            darkTheme = false
        ) {
            AppButton(text = "Secondary Button", onClick = {}, variant = AppButtonVariant.SECONDARY)
        }
    }

    @Test
    fun destructiveButton_light() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "AppButton_Destructive",
            variant = AppButtonVariant.DESTRUCTIVE,
            darkTheme = false
        ) {
            AppButton(text = "Destructive Button", onClick = {}, variant = AppButtonVariant.DESTRUCTIVE)
        }
    }

    @Test
    @Config(qualifiers = "w1280dp-h800dp-xhdpi")
    fun primaryButton_tablet() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "AppButton_Primary_Tablet",
            device = DefaultTestDevices.Tablet
        ) {
            AppButton(text = "Primary Button Tablet", onClick = {})
        }
    }

    @Test
    fun primaryButton_arabic() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "AppButton_Primary_Arabic",
            locale = Locale("ar")
        ) {
            AppButton(text = "زر أساسي", onClick = {})
        }
    }
}

/**
 * Extension for cleaner test calls.
 */
private fun captureAppScreenshot(
    composeTestRule: androidx.compose.ui.test.junit4.ComposeContentTestRule,
    name: String,
    device: DefaultTestDevices = DefaultTestDevices.Phone,
    locale: Locale = Locale.ENGLISH,
    darkTheme: Boolean = false,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    content: @Composable () -> Unit
) {
    com.aragabz.androidtemplate.core.ui.util.captureAppScreenshot(
        composeTestRule = composeTestRule,
        name = name,
        device = device,
        locale = locale,
        darkTheme = darkTheme,
        content = content
    )
}
