package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AuthScreenContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun unauthenticatedState_showsSignInPrompt() {
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

    @Test
    fun authenticatedState_showsSignedInUser() {
        composeTestRule.setContent {
            AppTheme {
                AuthScreenContent(
                    uiState = AuthUiState(
                        isAuthenticated = true,
                        currentUserId = "ragab",
                        userIdInput = "ragab",
                    ),
                    onEvent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Signed in as ragab").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Out").assertIsDisplayed()
    }
}
