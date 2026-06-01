package com.aragabz.androidtemplate.feature.todos.domain.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for todos operations.
 */
interface TodosRepository {
    /**
     * Get all todos.
     */
    fun getTodos(): Flow<AppResult<List<Todo>>>

    /**
     * Get a single todo by ID.
     */
    fun getTodoById(id: String): Flow<AppResult<Todo>>

    /**
     * Add a new todo.
     */
    fun addTodo(
        title: String,
        description: String?,
    ): Flow<AppResult<Todo>>

    /**
     * Toggle todo completion status.
     */
    fun toggleTodo(id: String): Flow<AppResult<Todo>>

    /**
     * Delete a todo.
     */
    fun deleteTodo(id: String): Flow<AppResult<Unit>>
}
