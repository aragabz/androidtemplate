package com.aragabz.androidtemplate.feature.todos.domain.usecase

import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import javax.inject.Inject

/**
 * Use case for toggling todo completion.
 */
class ToggleTodoUseCase
    @Inject
    constructor(
        private val repository: TodosRepository,
    ) {
        operator fun invoke(id: String) = repository.toggleTodo(id)
    }
