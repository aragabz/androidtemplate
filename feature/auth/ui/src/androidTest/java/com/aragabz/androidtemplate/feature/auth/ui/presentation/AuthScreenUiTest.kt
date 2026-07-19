package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun authContent_showsSignInAction() {
        composeTestRule.setContent {
            AppTheme {
                AuthScreenContent(
                    uiState = AuthUiState(userIdInput = "ragab"),
                    onEvent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Authentication").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed().assertIsEnabled()
    }
}
