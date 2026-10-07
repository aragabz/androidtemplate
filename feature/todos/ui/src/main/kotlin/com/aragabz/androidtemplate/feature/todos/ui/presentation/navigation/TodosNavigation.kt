package com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo.AddTodoScreen
import com.aragabz.androidtemplate.feature.todos.ui.presentation.details.TodoDetailsScreen
import kotlinx.serialization.Serializable

/** Add todo screen. */
@Serializable
data object AddTodoRoute

/** Todo details screen for the todo with [id]. */
@Serializable
data class TodoDetailsRoute(
    val id: String,
)

/**
 * Registers the todos feature's screens. The todos list itself is hosted by the app's bottom-nav shell.
 */
fun NavGraphBuilder.todosScreen(navController: NavController) {
    composable<AddTodoRoute> {
        AddTodoScreen(onBack = { navController.popBackStack() })
    }

    composable<TodoDetailsRoute> {
        TodoDetailsScreen(onBack = { navController.popBackStack() })
    }
}

/**
 * Navigate to add todo screen.
 */
fun NavController.navigateToAddTodo() {
    navigate(AddTodoRoute)
}

/**
 * Navigate to todo details screen.
 */
fun NavController.navigateToTodoDetails(todoId: String) {
    navigate(TodoDetailsRoute(todoId))
}
