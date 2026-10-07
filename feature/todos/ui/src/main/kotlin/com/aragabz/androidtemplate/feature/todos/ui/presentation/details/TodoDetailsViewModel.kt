package com.aragabz.androidtemplate.feature.todos.ui.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.core.ui.text.errorUiText
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.usecase.DeleteTodoUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.GetTodoByIdUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.ToggleTodoUseCase
import com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation.TodoDetailsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for todo details screen. The todo is loaded once; a toggle replaces it with the updated todo.
 */
@HiltViewModel
class TodoDetailsViewModel
    @Inject
    constructor(
        getTodoByIdUseCase: GetTodoByIdUseCase,
        private val toggleTodoUseCase: ToggleTodoUseCase,
        private val deleteTodoUseCase: DeleteTodoUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val todoId: String = savedStateHandle.toRoute<TodoDetailsRoute>().id

        private val updatedTodo = MutableStateFlow<Todo?>(null)
        private val error = MutableStateFlow<UiText?>(null)
        private val isDeleted = MutableStateFlow(false)

        private val loadState =
            getTodoByIdUseCase(todoId).runningFold(TodoDetailsUiState(isLoading = true)) { state, result ->
                when (result) {
                    is AppResult.Loading -> state.copy(isLoading = true)
                    is AppResult.Success -> state.copy(todo = result.data, isLoading = false)
                    is AppResult.Error -> {
                        error.value = result.errorUiText
                        state.copy(isLoading = false)
                    }
                }
            }

        val uiState: StateFlow<TodoDetailsUiState> =
            combine(loadState, updatedTodo, error, isDeleted) { state, updatedTodo, error, isDeleted ->
                state.copy(todo = updatedTodo ?: state.todo, error = error, isDeleted = isDeleted)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = TodoDetailsUiState(isLoading = true),
            )

        fun onEvent(event: TodoDetailsEvent) {
            when (event) {
                TodoDetailsEvent.OnToggleTodo -> toggleTodo()
                TodoDetailsEvent.OnDeleteTodo -> deleteTodo()
                TodoDetailsEvent.OnDismissError -> error.value = null
            }
        }

        private fun toggleTodo() {
            viewModelScope.launch {
                toggleTodoUseCase(todoId).collect { result ->
                    when (result) {
                        is AppResult.Success -> updatedTodo.value = result.data
                        is AppResult.Error -> error.value = result.errorUiText
                        is AppResult.Loading -> Unit
                    }
                }
            }
        }

        private fun deleteTodo() {
            viewModelScope.launch {
                deleteTodoUseCase(todoId).collect { result ->
                    when (result) {
                        is AppResult.Success -> isDeleted.value = true
                        is AppResult.Error -> error.value = result.errorUiText
                        is AppResult.Loading -> Unit
                    }
                }
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
