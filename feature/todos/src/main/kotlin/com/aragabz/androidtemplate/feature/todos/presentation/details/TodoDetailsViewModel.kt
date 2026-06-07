package com.aragabz.androidtemplate.feature.todos.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.todos.domain.usecase.DeleteTodoUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.GetTodoByIdUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.ToggleTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for todo details screen.
 */
@HiltViewModel
class TodoDetailsViewModel
    @Inject
    constructor(
        private val getTodoByIdUseCase: GetTodoByIdUseCase,
        private val toggleTodoUseCase: ToggleTodoUseCase,
        private val deleteTodoUseCase: DeleteTodoUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val todoRoute: Route.TodoDetails = savedStateHandle.toRoute()
        private val todoId: String = todoRoute.id

        private val _uiState = MutableStateFlow(TodoDetailsUiState())
        val uiState: StateFlow<TodoDetailsUiState> = _uiState.asStateFlow()

        private val _navigationEvents = Channel<TodoDetailsNavigationEvent>()
        val navigationEvents = _navigationEvents.receiveAsFlow()

        init {
            loadTodo()
        }

        fun onEvent(event: TodoDetailsEvent) {
            when (event) {
                TodoDetailsEvent.OnToggleTodo -> toggleTodo()
                TodoDetailsEvent.OnDeleteTodo -> deleteTodo()
                TodoDetailsEvent.OnDismissError -> dismissError()
            }
        }

        private fun loadTodo() {
            viewModelScope.launch {
                getTodoByIdUseCase(todoId).collect { result ->
                    when (result) {
                        is AppResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }
                        is AppResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    todo = result.data,
                                    isLoading = false,
                                    error = null,
                                )
                            }
                        }
                        is AppResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Failed to load todo",
                                )
                            }
                        }
                    }
                }
            }
        }

        private fun toggleTodo() {
            viewModelScope.launch {
                toggleTodoUseCase(todoId).collect { result ->
                    when (result) {
                        is AppResult.Success -> loadTodo()
                        is AppResult.Error -> {
                            _uiState.update { it.copy(error = "Failed to update todo") }
                        }
                        is AppResult.Loading -> { /* No UI change */ }
                    }
                }
            }
        }

        private fun deleteTodo() {
            viewModelScope.launch {
                deleteTodoUseCase(todoId).collect { result ->
                    when (result) {
                        is AppResult.Success -> {
                            _navigationEvents.send(TodoDetailsNavigationEvent.NavigateBack)
                        }
                        is AppResult.Error -> {
                            _uiState.update { it.copy(error = "Failed to delete todo") }
                        }
                        is AppResult.Loading -> { /* No UI change */ }
                    }
                }
            }
        }

        private fun dismissError() {
            _uiState.update { it.copy(error = null) }
        }
    }

/**
 * Navigation events for todo details screen.
 */
sealed interface TodoDetailsNavigationEvent {
    data object NavigateBack : TodoDetailsNavigationEvent
}
