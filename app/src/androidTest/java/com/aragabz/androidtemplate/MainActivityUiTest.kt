package com.aragabz.androidtemplate

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launch_showsHomeDashboard() {
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Dashboard View").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Dashboard View").assertIsDisplayed()
    }
}
