package com.aragabz.androidtemplate.feature.todos.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of TodosRepository.
 * Uses in-memory list for demo purposes.
 */
@Singleton
class TodosRepositoryImpl
    @Inject
    constructor() : TodosRepository {
        private val todos =
            mutableListOf(
                Todo(
                    id = "1",
                    title = "Welcome to Todos!",
                    description = "This is your first todo item",
                    isCompleted = false,
                    createdAt = "2024-06-01T00:00:00Z",
                ),
                Todo(
                    id = "2",
                    title = "Learn Jetpack Compose",
                    description = "Build beautiful UIs with Compose",
                    isCompleted = true,
                    createdAt = "2024-06-01T00:00:00Z",
                ),
                Todo(
                    id = "3",
                    title = "Implement Clean Architecture",
                    description = "Follow SOLID principles",
                    isCompleted = false,
                    createdAt = "2024-06-01T00:00:00Z",
                ),
            )

        override fun getTodos(): Flow<AppResult<List<Todo>>> =
            flow {
                emit(AppResult.Loading)
                delay(300) // Simulate network delay
                emit(AppResult.Success(todos.toList()))
            }

        override fun getTodoById(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                delay(200)

                val todo = todos.firstOrNull { it.id == id }
                if (todo != null) {
                    emit(AppResult.Success(todo))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }

        override fun addTodo(
            title: String,
            description: String?,
        ): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                delay(200)

                val newTodo =
                    Todo(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        isCompleted = false,
                        createdAt = System.currentTimeMillis().toString(),
                    )
                todos.add(0, newTodo) // Add at top
                emit(AppResult.Success(newTodo))
            }

        override fun toggleTodo(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                delay(150)

                val index = todos.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updated =
                        todos[index].copy(
                            isCompleted = !todos[index].isCompleted,
                            updatedAt = System.currentTimeMillis().toString(),
                        )
                    todos[index] = updated
                    emit(AppResult.Success(updated))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }

        override fun deleteTodo(id: String): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                delay(150)

                val removed = todos.removeIf { it.id == id }
                if (removed) {
                    emit(AppResult.Success(Unit))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }
    }
