package com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo

import com.aragabz.androidtemplate.core.ui.text.UiText

/**
 * UI state for add todo screen.
 *
 * @property isSaved true once the todo is saved; the screen then navigates back.
 */
data class AddTodoUiState(
    val title: String = "",
    val description: String = "",
    val titleError: UiText? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isSaved: Boolean = false,
)
