package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.feature.todos.domain.usecase.DeleteTodoUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.GetTodosUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.ToggleTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for todos screen.
 *
 * The list is one collection of the repository's stream, restarted only by [TodosEvent.OnRefresh]. Toggling or
 * deleting writes to the database, and the stream emits the change; nothing re-subscribes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodosViewModel
    @Inject
    constructor(
        private val getTodosUseCase: GetTodosUseCase,
        private val toggleTodoUseCase: ToggleTodoUseCase,
        private val deleteTodoUseCase: DeleteTodoUseCase,
    ) : ViewModel() {
        private val refreshRequests = MutableStateFlow(0)
        private val error = MutableStateFlow<UiText?>(null)

        private val todosState: Flow<TodosUiState> =
            refreshRequests
                .flatMapLatest { getTodosUseCase() }
                .runningFold(TodosUiState(isLoading = true)) { state, result ->
                    when (result) {
                        is AppResult.Loading -> state.copy(isLoading = true)
                        is AppResult.Success -> state.copy(todos = result.data, isLoading = false)
                        is AppResult.Error -> {
                            error.value = result.errorUiText
                            state.copy(isLoading = false)
                        }
                    }
                }

        val uiState: StateFlow<TodosUiState> =
            combine(todosState, error) { state, error -> state.copy(error = error) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = TodosUiState(isLoading = true),
                )

        fun onEvent(event: TodosEvent) {
            when (event) {
                TodosEvent.OnRefresh -> {
                    error.value = null
                    refreshRequests.update { it + 1 }
                }
                is TodosEvent.OnToggleTodo -> runAction { toggleTodoUseCase(event.id) }
                is TodosEvent.OnDeleteTodo -> runAction { deleteTodoUseCase(event.id) }
                TodosEvent.OnDismissError -> error.value = null
            }
        }

        private fun runAction(action: () -> Flow<AppResult<*>>) {
            viewModelScope.launch {
                action().collect { result ->
                    if (result is AppResult.Error) error.value = result.errorUiText
                }
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
