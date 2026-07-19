package com.aragabz.androidtemplate.core.designsystem.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
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
class AppThemeScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appTheme_light() {
        captureTheme("AppTheme_Light", darkTheme = false)
    }

    @Test
    fun appTheme_dark() {
        captureTheme("AppTheme_Dark", darkTheme = true)
    }

    private fun captureTheme(name: String, darkTheme: Boolean) {
        composeTestRule.setContent {
            AppTheme(darkTheme = darkTheme) {
                Surface {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(LocalSpacing.current.large),
                        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium),
                    ) {
                        Text("Theme Preview", style = MaterialTheme.typography.headlineMedium)
                        Text("Dark theme active: ${LocalIsDarkTheme.current}")
                        Text(
                            text = "Primary color sample",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/test/snapshots/${name}.png",
        )
    }
}
