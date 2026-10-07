package com.aragabz.androidtemplate.feature.profile.ui.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProfileScreenContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_promptsUserToSignIn() {
        composeTestRule.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState = ProfileUiState(profile = null),
                    onEvent = {},
                    onSignOut = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No active profile").assertIsDisplayed()
    }

    @Test
    fun loadedState_showsProfileFields() {
        composeTestRule.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState =
                        ProfileUiState(
                            profile =
                                UserProfile(
                                    userId = "ragab",
                                    displayName = "Ragab",
                                    email = "ragab@example.com",
                                ),
                        ),
                    onEvent = {},
                    onSignOut = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("User ID: ragab").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email: ragab@example.com").assertIsDisplayed()
    }
}
