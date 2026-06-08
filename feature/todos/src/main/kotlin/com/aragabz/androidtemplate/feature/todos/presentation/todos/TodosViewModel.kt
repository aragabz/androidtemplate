package com.aragabz.androidtemplate.feature.todos.presentation.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.domain.usecase.DeleteTodoUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.GetTodosUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.ToggleTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for todos screen.
 */
@HiltViewModel
class TodosViewModel
    @Inject
    constructor(
        private val getTodosUseCase: GetTodosUseCase,
        private val toggleTodoUseCase: ToggleTodoUseCase,
        private val deleteTodoUseCase: DeleteTodoUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(TodosUiState())
        val uiState: StateFlow<TodosUiState> = _uiState.asStateFlow()

        init {
            loadTodos()
        }

        fun onEvent(event: TodosEvent) {
            when (event) {
                TodosEvent.OnRefresh -> loadTodos()
                is TodosEvent.OnToggleTodo -> toggleTodo(event.id)
                is TodosEvent.OnDeleteTodo -> deleteTodo(event.id)
                TodosEvent.OnDismissError -> dismissError()
            }
        }

        private fun loadTodos() {
            viewModelScope.launch {
                getTodosUseCase().collect { result ->
                    when (result) {
                        is AppResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }
                        is AppResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    todos = result.data,
                                    isLoading = false,
                                    error = null,
                                )
                            }
                        }
                        is AppResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.errorUiText,
                                )
                            }
                        }
                    }
                }
            }
        }

        private fun toggleTodo(id: String) {
            viewModelScope.launch {
                toggleTodoUseCase(id).collect { result ->
                    when (result) {
                        is AppResult.Success -> loadTodos()
                        is AppResult.Error -> {
                            _uiState.update { it.copy(error = result.errorUiText) }
                        }
                        is AppResult.Loading -> { /* No UI change during toggle */ }
                    }
                }
            }
        }

        private fun deleteTodo(id: String) {
            viewModelScope.launch {
                deleteTodoUseCase(id).collect { result ->
                    when (result) {
                        is AppResult.Success -> loadTodos()
                        is AppResult.Error -> {
                            _uiState.update { it.copy(error = result.errorUiText) }
                        }
                        is AppResult.Loading -> { /* No UI change during delete */ }
                    }
                }
            }
        }

        private fun dismissError() {
            _uiState.update { it.copy(error = null) }
        }
    }
