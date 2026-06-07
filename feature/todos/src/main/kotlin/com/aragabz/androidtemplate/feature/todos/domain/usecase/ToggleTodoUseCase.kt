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
 * Use case for toggling todo completion status.
 */
class ToggleTodoUseCase
    @Inject
    constructor(
        private val repository: TodosRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : FlowUseCase<String, AppResult<Todo>>(dispatcher) {
        override fun execute(parameters: String): Flow<AppResult<Todo>> = repository.toggleTodo(parameters)
    }
