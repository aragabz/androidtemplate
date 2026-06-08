package com.aragabz.androidtemplate.feature.todos.presentation.addtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.domain.usecase.AddTodoUseCase
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
 * ViewModel for add todo screen.
 */
@HiltViewModel
class AddTodoViewModel
    @Inject
    constructor(
        private val addTodoUseCase: AddTodoUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AddTodoUiState())
        val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

        private val _navigationEvents = Channel<AddTodoNavigationEvent>()
        val navigationEvents = _navigationEvents.receiveAsFlow()

        fun onEvent(event: AddTodoEvent) {
            when (event) {
                is AddTodoEvent.OnTitleChanged -> updateTitle(event.title)
                is AddTodoEvent.OnDescriptionChanged -> updateDescription(event.description)
                AddTodoEvent.OnSaveClicked -> saveTodo()
                AddTodoEvent.OnDismissError -> dismissError()
            }
        }

        private fun updateTitle(title: String) {
            _uiState.update {
                it.copy(
                    title = title,
                    titleError = if (title.isNotBlank()) null else it.titleError,
                )
            }
        }

        private fun updateDescription(description: String) {
            _uiState.update { it.copy(description = description) }
        }

        private fun saveTodo() {
            val currentState = _uiState.value

            // Validate
            if (currentState.title.isBlank()) {
                _uiState.update { it.copy(titleError = "Title is required") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                addTodoUseCase(
                    AddTodoUseCase.Params(
                        title = currentState.title.trim(),
                        description = currentState.description.trim().takeIf { it.isNotEmpty() },
                    ),
                ).collect { result ->
                    when (result) {
                        is AppResult.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _navigationEvents.send(AddTodoNavigationEvent.NavigateBack)
                        }
                        is AppResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.errorUiText,
                                )
                            }
                        }
                        is AppResult.Loading -> {
                            // Keep isLoading = true
                        }
                    }
                }
            }
        }

        private fun dismissError() {
            _uiState.update { it.copy(error = null) }
        }
    }

/**
 * Navigation events for add todo screen.
 */
sealed interface AddTodoNavigationEvent {
    data object NavigateBack : AddTodoNavigationEvent
}
