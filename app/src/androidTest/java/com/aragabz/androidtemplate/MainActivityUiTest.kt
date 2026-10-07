package com.aragabz.androidtemplate

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import com.aragabz.androidtemplate.feature.auth.ui.R as AuthR

class MainActivityUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launch_signedOut_showsSignIn() {
        val signInTitle = composeTestRule.activity.getString(AuthR.string.auth_title_sign_in)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(signInTitle).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(signInTitle).assertIsDisplayed()
    }
}
