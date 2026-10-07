package com.aragabz.androidtemplate.feature.todos.ui.presentation.details

import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo

/**
 * UI state for todo details screen.
 *
 * @property isDeleted true once the todo is deleted; the screen then navigates back.
 */
data class TodoDetailsUiState(
    val todo: Todo? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isDeleted: Boolean = false,
)
