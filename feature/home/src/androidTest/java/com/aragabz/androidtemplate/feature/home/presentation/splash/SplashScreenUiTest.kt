package com.aragabz.androidtemplate.feature.home.presentation.splash

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class SplashScreenUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun splashContent_showsBranding() {
        composeTestRule.setContent {
            AppTheme {
                SplashScreenContent(scale = 1f)
            }
        }

        composeTestRule.onNodeWithText("Android Template").assertIsDisplayed()
    }
}
