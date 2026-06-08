package com.aragabz.androidtemplate.feature.todos.presentation.todos

import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo

/**
 * UI state for todos screen.
 */
data class TodosUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
