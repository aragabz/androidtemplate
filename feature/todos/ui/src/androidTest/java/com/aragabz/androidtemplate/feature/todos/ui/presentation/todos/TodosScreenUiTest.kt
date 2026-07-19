package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodosScreenUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun todosContent_emptyStateShowsPrompt() {
        composeTestRule.setContent {
            AppTheme {
                TodosScreenContent(
                    uiState = TodosUiState(),
                    errorMessage = null,
                    onAddTodo = {},
                    onTodoClick = {},
                    onToggleTodo = {},
                    onDeleteTodo = {},
                    onRefresh = {},
                    onDismissError = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeTestRule.onNodeWithText("No todos yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Todo").assertIsDisplayed()
    }
}
