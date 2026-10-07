package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.feature.auth.ui.R
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
                    uiState = AuthUiState(email = "ragab@example.com", password = "password123"),
                    onEvent = {},
                )
            }
        }

        val activity = composeTestRule.activity
        composeTestRule.onNodeWithText(activity.getString(R.string.auth_title_sign_in)).assertIsDisplayed()
        composeTestRule
            .onNodeWithText(activity.getString(R.string.auth_sign_in))
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
