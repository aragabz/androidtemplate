package com.aragabz.androidtemplate.core.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import com.aragabz.androidtemplate.core.ui.util.captureAppScreenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w360dp-h640dp-xhdpi")
class StateScreensScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingScreen_light() {
        captureAppScreenshot(composeTestRule = composeTestRule, name = "LoadingScreen") {
            LoadingScreen(message = "Loading todos")
        }
    }

    @Test
    fun emptyScreen_dark() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "EmptyScreen",
            darkTheme = true,
        ) {
            EmptyScreen(
                message = "No todos yet",
                subtitle = "Tap add to create your first task",
                actionLabel = "Add Todo",
                onAction = {},
            )
        }
    }

    @Test
    fun errorScreen_light() {
        captureAppScreenshot(composeTestRule = composeTestRule, name = "ErrorScreen") {
            ErrorScreen(
                message = "Unable to load your tasks right now.",
                onRetry = {},
            )
        }
    }

    @Test
    fun successScreen_dark() {
        captureAppScreenshot(
            composeTestRule = composeTestRule,
            name = "SuccessScreen",
            darkTheme = true,
        ) {
            SuccessScreen(
                message = "All todos completed",
                subtitle = "Great work. You can add another todo anytime.",
                actionLabel = "Add Todo",
                onAction = {},
            )
        }
    }
}
