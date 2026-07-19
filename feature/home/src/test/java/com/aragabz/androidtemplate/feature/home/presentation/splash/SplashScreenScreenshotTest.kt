package com.aragabz.androidtemplate.feature.home.presentation.splash

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w360dp-h640dp-xhdpi")
class SplashScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splashScreen_light() {
        composeTestRule.setContent {
            AppTheme(darkTheme = false) {
                SplashScreenContent(scale = 1f)
            }
        }

        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/test/snapshots/SplashScreen_Light.png",
        )
    }
}
