package com.aragabz.androidtemplate.feature.todos.presentation.addtodo

import com.aragabz.androidtemplate.core.common.ui.UiText

/**
 * UI state for add todo screen.
 */
data class AddTodoUiState(
    val title: String = "",
    val description: String = "",
    val titleError: String? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
