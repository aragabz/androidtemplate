package com.aragabz.androidtemplate.feature.todos.presentation.addtodo

/**
 * Events for add todo screen.
 */
sealed interface AddTodoEvent {
    data class OnTitleChanged(val title: String) : AddTodoEvent
    data class OnDescriptionChanged(val description: String) : AddTodoEvent
    data object OnSaveClicked : AddTodoEvent
    data object OnDismissError : AddTodoEvent
}
