package com.aragabz.androidtemplate.feature.todos.ui.presentation.details

/**
 * Events for todo details screen.
 */
sealed interface TodoDetailsEvent {
    data object OnToggleTodo : TodoDetailsEvent

    data object OnDeleteTodo : TodoDetailsEvent

    data object OnDismissError : TodoDetailsEvent
}
