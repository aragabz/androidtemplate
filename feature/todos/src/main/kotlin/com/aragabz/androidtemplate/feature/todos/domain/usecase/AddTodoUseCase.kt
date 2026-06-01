package com.aragabz.androidtemplate.feature.todos.domain.usecase

import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import javax.inject.Inject

/**
 * Use case for adding a new todo.
 */
class AddTodoUseCase
    @Inject
    constructor(
        private val repository: TodosRepository,
    ) {
        operator fun invoke(
            title: String,
            description: String?,
        ) = repository.addTodo(title, description)
    }
