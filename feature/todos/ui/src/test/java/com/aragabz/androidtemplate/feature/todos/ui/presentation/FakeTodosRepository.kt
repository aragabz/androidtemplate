package com.aragabz.androidtemplate.feature.todos.ui.presentation

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * In-memory [TodosRepository] whose list is a live stream, like the Room-backed one, and which counts how often
 * the list is subscribed to.
 */
class FakeTodosRepository(
    initial: List<Todo> = emptyList(),
) : TodosRepository {
    val todos = MutableStateFlow(initial)

    var getTodosCalls = 0
        private set

    /** When set, mutations fail with this error instead of changing [todos]. */
    var failure: Throwable? = null

    override fun getTodos(): Flow<AppResult<List<Todo>>> {
        getTodosCalls++
        return todos
            .map<List<Todo>, AppResult<List<Todo>>> {
                AppResult.Success(
                    it,
                )
            }.onStart { emit(AppResult.Loading) }
    }

    override fun getTodoById(id: String): Flow<AppResult<Todo>> =
        flow {
            emit(AppResult.Loading)
            emit(
                todos.first().find { it.id == id }?.let { AppResult.Success(it) }
                    ?: AppResult.Error(NoSuchElementException(id)),
            )
        }

    override fun addTodo(
        title: String,
        description: String?,
    ): Flow<AppResult<Todo>> =
        mutate {
            val todo = Todo(id = "id-${todos.value.size + 1}", title = title, description = description, createdAt = 0L)
            todos.value += todo
            todo
        }

    override fun toggleTodo(id: String): Flow<AppResult<Todo>> =
        mutate {
            val toggled = todos.value.first { it.id == id }.let { it.copy(isCompleted = !it.isCompleted) }
            todos.value = todos.value.map { if (it.id == id) toggled else it }
            toggled
        }

    override fun deleteTodo(id: String): Flow<AppResult<Unit>> =
        mutate {
            todos.value =
                todos.value.filterNot { it.id == id }
        }

    private fun <T> mutate(block: () -> T): Flow<AppResult<T>> {
        val error = failure
        return if (error != null) {
            flowOf(AppResult.Loading, AppResult.Error(error))
        } else {
            flow {
                emit(AppResult.Loading)
                emit(AppResult.Success(block()))
            }
        }
    }
}
