package com.aragabz.androidtemplate.feature.profile.ui.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun profileContent_showsProfileDetails() {
        composeTestRule.setContent {
            AppTheme {
                ProfileScreenContent(
                    uiState = ProfileUiState(profile = UserProfile("ragab", "Ragab", "ragab@example.com")),
                    onEvent = {},
                    onSignOut = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("User ID: ragab").assertIsDisplayed()
    }
}
