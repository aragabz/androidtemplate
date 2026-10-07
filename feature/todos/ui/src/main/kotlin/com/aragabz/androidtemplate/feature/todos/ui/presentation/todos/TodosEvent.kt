package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

/**
 * Events for todos screen.
 */
sealed interface TodosEvent {
    data object OnRefresh : TodosEvent

    data class OnToggleTodo(
        val id: String,
    ) : TodosEvent

    data class OnDeleteTodo(
        val id: String,
    ) : TodosEvent

    data object OnDismissError : TodosEvent
}
