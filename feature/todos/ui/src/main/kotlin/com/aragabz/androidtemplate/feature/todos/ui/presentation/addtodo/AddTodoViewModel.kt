package com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.core.ui.text.errorUiText
import com.aragabz.androidtemplate.feature.todos.domain.usecase.AddTodoUseCase
import com.aragabz.androidtemplate.feature.todos.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for add todo screen. A form's state has no outside source, so it is held in a [MutableStateFlow].
 */
@HiltViewModel
class AddTodoViewModel
    @Inject
    constructor(
        private val addTodoUseCase: AddTodoUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AddTodoUiState())
        val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

        fun onEvent(event: AddTodoEvent) {
            when (event) {
                is AddTodoEvent.OnTitleChanged -> updateTitle(event.title)
                is AddTodoEvent.OnDescriptionChanged -> _uiState.update { it.copy(description = event.description) }
                AddTodoEvent.OnSaveClicked -> saveTodo()
                AddTodoEvent.OnDismissError -> _uiState.update { it.copy(error = null) }
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

        private fun saveTodo() {
            val currentState = _uiState.value

            if (currentState.title.isBlank()) {
                _uiState.update { it.copy(titleError = UiText.StringResource(R.string.todo_title_required)) }
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
                        is AppResult.Success -> _uiState.update { it.copy(isLoading = false, isSaved = true) }
                        is AppResult.Error -> _uiState.update {
                            it.copy(isLoading = false, error = result.errorUiText)
                        }
                        is AppResult.Loading -> Unit
                    }
                }
            }
        }
    }
