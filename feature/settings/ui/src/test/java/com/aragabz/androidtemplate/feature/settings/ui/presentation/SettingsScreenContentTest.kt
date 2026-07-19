package com.aragabz.androidtemplate.feature.settings.ui.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SettingsScreenContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsState_showsSelectedThemeAndLanguage() {
        composeTestRule.setContent {
            AppTheme {
                SettingsScreenContent(
                    uiState =
                        SettingsUiState(
                            selectedTheme = ThemePreference.DARK,
                            selectedLanguage = "ar",
                        ),
                    onEvent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("DARK (selected)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Language").assertIsDisplayed()
    }
}
