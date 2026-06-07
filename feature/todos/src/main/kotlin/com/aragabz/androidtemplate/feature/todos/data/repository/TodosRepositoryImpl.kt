package com.aragabz.androidtemplate.feature.todos.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TodosRepository isolating todos per account in-memory.
 */
@Singleton
class TodosRepositoryImpl
    @Inject
    constructor(
        private val preferencesRepository: UserPreferencesRepository,
    ) : TodosRepository {
        private val todosMap = mutableMapOf<String, MutableList<Todo>>()

        private suspend fun getActiveUserId(): String {
            return preferencesRepository.userPreferences.first().userId ?: "default"
        }

        private fun getOrCreateUserTodos(userId: String): MutableList<Todo> {
            return todosMap.getOrPut(userId) {
                mutableListOf(
                    Todo(
                        id = "1",
                        title = "Welcome to Android Template!",
                        description = "This is your first todo item in this account.",
                        isCompleted = false,
                        createdAt = "2024-06-01T00:00:00Z",
                    ),
                    Todo(
                        id = "2",
                        title = "Add a new account",
                        description = "Go to settings and create a new account to test switching.",
                        isCompleted = false,
                        createdAt = "2024-06-01T00:00:00Z",
                    ),
                )
            }
        }

        override fun getTodos(): Flow<AppResult<List<Todo>>> =
            flow {
                emit(AppResult.Loading)
                delay(DELAY_LONG) // Simulate network/db delay
                val userId = getActiveUserId()
                val list = getOrCreateUserTodos(userId)
                emit(AppResult.Success(list.toList()))
            }

        override fun getTodoById(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                delay(DELAY_SHORT)
                val userId = getActiveUserId()
                val list = getOrCreateUserTodos(userId)
                val todo = list.firstOrNull { it.id == id }
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
                delay(DELAY_SHORT)
                val userId = getActiveUserId()
                val list = getOrCreateUserTodos(userId)
                val newTodo =
                    Todo(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        isCompleted = false,
                        createdAt = System.currentTimeMillis().toString(),
                    )
                list.add(0, newTodo) // Add at top
                emit(AppResult.Success(newTodo))
            }

        override fun toggleTodo(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                delay(DELAY_SHORT)
                val userId = getActiveUserId()
                val list = getOrCreateUserTodos(userId)
                val index = list.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updated =
                        list[index].copy(
                            isCompleted = !list[index].isCompleted,
                            updatedAt = System.currentTimeMillis().toString(),
                        )
                    list[index] = updated
                    emit(AppResult.Success(updated))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }

        override fun deleteTodo(id: String): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                delay(DELAY_SHORT)
                val userId = getActiveUserId()
                val list = getOrCreateUserTodos(userId)
                val removed = list.removeIf { it.id == id }
                if (removed) {
                    emit(AppResult.Success(Unit))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }

        companion object {
            private const val DELAY_LONG = 150L
            private const val DELAY_SHORT = 100L
        }
    }
