package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TodosScreenContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsAddPrompt() {
        composeTestRule.setContent {
            AppTheme {
                TodosScreenContent(
                    uiState = TodosUiState(),
                    onEvent = {},
                    onAddTodo = {},
                    onTodoClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No todos yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Todo").assertIsDisplayed()
    }

    @Test
    fun completedState_showsSuccessMessage() {
        composeTestRule.setContent {
            AppTheme {
                TodosScreenContent(
                    uiState =
                        TodosUiState(
                            todos =
                                listOf(
                                    Todo(
                                        id = "1",
                                        title = "Ship tests",
                                        description = null,
                                        isCompleted = true,
                                        createdAt = System.currentTimeMillis(),
                                        updatedAt = null,
                                    ),
                                ),
                        ),
                    onEvent = {},
                    onAddTodo = {},
                    onTodoClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("All todos completed").assertIsDisplayed()
    }
}
