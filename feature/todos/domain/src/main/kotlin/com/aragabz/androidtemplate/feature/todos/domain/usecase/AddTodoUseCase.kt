package com.aragabz.androidtemplate.feature.todos.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.FlowUseCase
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for adding a new todo.
 */
class AddTodoUseCase
    @Inject
    constructor(
        private val repository: TodosRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : FlowUseCase<AddTodoUseCase.Params, AppResult<Todo>>(dispatcher) {
        data class Params(val title: String, val description: String?)

        override fun execute(parameters: Params): Flow<AppResult<Todo>> =
            repository.addTodo(parameters.title, parameters.description)
    }
