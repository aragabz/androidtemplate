package com.aragabz.androidtemplate.feature.todos.presentation.details

/**
 * Events for todo details screen.
 */
sealed interface TodoDetailsEvent {
    data object OnToggleTodo : TodoDetailsEvent

    data object OnDeleteTodo : TodoDetailsEvent

    data object OnDismissError : TodoDetailsEvent
}
