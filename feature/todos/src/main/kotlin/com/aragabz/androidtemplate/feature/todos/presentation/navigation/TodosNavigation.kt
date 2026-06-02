package com.aragabz.androidtemplate.feature.todos.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.todos.presentation.addtodo.AddTodoScreen
import com.aragabz.androidtemplate.feature.todos.presentation.details.TodoDetailsScreen
import com.aragabz.androidtemplate.feature.todos.presentation.todos.TodosScreen

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
