package com.aragabz.androidtemplate.feature.todos.domain.usecase

import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import javax.inject.Inject

/**
 * Use case for getting all todos.
 */
class GetTodosUseCase
    @Inject
    constructor(
        private val repository: TodosRepository,
    ) {
        operator fun invoke() = repository.getTodos()
    }
