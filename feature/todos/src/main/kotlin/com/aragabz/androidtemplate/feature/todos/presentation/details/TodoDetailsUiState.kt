package com.aragabz.androidtemplate.feature.todos.presentation.details

import com.aragabz.androidtemplate.feature.todos.domain.model.Todo

/**
 * UI state for todo details screen.
 */
data class TodoDetailsUiState(
    val todo: Todo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
