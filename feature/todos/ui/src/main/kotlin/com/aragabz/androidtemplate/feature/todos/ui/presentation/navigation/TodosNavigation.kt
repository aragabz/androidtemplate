package com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo.AddTodoScreen
import com.aragabz.androidtemplate.feature.todos.ui.presentation.details.TodoDetailsScreen
import com.aragabz.androidtemplate.feature.todos.ui.presentation.todos.TodosScreen

/**
 * Navigation extension for todos feature.
 */
fun NavGraphBuilder.todosScreen(navController: NavController) {
    // Todos list screen (home)
    composable<Route.Todos> {
        TodosScreen(navController = navController)
    }

    // Add todo screen
    composable<Route.AddTodo> {
        AddTodoScreen(navController = navController)
    }

    // Todo details screen
    composable<Route.TodoDetails> {
        TodoDetailsScreen(navController = navController)
    }
}

/**
 * Navigate to todos screen.
 */
fun NavController.navigateToTodos() {
    navigate(Route.Todos) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Navigate to add todo screen.
 */
fun NavController.navigateToAddTodo() {
    navigate(Route.AddTodo)
}

/**
 * Navigate to todo details screen.
 */
fun NavController.navigateToTodoDetails(todoId: String) {
    navigate(Route.TodoDetails(todoId))
}
