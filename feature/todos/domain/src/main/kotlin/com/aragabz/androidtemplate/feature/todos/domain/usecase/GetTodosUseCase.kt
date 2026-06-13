package com.aragabz.androidtemplate.feature.todos.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.NoParamFlowUseCase
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all todos.
 */
class GetTodosUseCase
    @Inject
    constructor(
        private val repository: TodosRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : NoParamFlowUseCase<AppResult<List<Todo>>>(dispatcher) {
        override fun execute(): Flow<AppResult<List<Todo>>> = repository.getTodos()
    }
