package com.aragabz.androidtemplate.feature.todos.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.data.local.dao.TodoDao
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TodosRepository using Room database.
 */
@Singleton
class TodosRepositoryImpl
    @Inject
    constructor(
        private val preferencesRepository: UserPreferencesRepository,
        private val todoDao: TodoDao,
    ) : TodosRepository {

        private suspend fun getActiveUserId(): String {
            return preferencesRepository.userPreferences.first().userId ?: "default"
        }

        override fun getTodos(): Flow<AppResult<List<Todo>>> =
            flow {
                emit(AppResult.Loading)
                val userId = getActiveUserId()

                // Add default todos if database is empty for this user
                val currentTodos = todoDao.getTodosByUserId(userId).first()
                if (currentTodos.isEmpty()) {
                    addDefaultTodos(userId)
                }

                emitAll(
                    todoDao.getTodosByUserId(userId)
                        .map { entities ->
                            AppResult.Success(entities.map { it.toExternalModel() })
                        }
                        .catch { emit(AppResult.Error(it as Exception)) }
                )
            }.catch { emit(AppResult.Error(it as Exception)) }

        private suspend fun addDefaultTodos(userId: String) {
            val defaults =
                listOf(
                    TodoEntity(
                        id = "1",
                        userId = userId,
                        title = "Welcome to Android Template!",
                        description = "This is your first todo item in this account.",
                        isCompleted = false,
                        createdAt = "2024-06-01T00:00:00Z",
                        updatedAt = null,
                    ),
                    TodoEntity(
                        id = "2",
                        userId = userId,
                        title = "Add a new account",
                        description = "Go to settings and create a new account to test switching.",
                        isCompleted = false,
                        createdAt = "2024-06-01T00:00:00Z",
                        updatedAt = null,
                    ),
                )
            todoDao.upsertAll(defaults)
        }

        override fun getTodoById(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                val todo = todoDao.getTodoById(id)
                if (todo != null) {
                    emit(AppResult.Success(todo.toExternalModel()))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }.catch { emit(AppResult.Error(it as Exception)) }

        override fun addTodo(
            title: String,
            description: String?,
        ): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                val userId = getActiveUserId()
                val newTodo =
                    TodoEntity(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = title,
                        description = description,
                        isCompleted = false,
                        createdAt = System.currentTimeMillis().toString(),
                        updatedAt = null,
                    )
                todoDao.upsert(newTodo)
                emit(AppResult.Success(newTodo.toExternalModel()))
            }.catch { emit(AppResult.Error(it as Exception)) }

        override fun toggleTodo(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                val todo = todoDao.getTodoById(id)
                if (todo != null) {
                    val updated =
                        todo.copy(
                            isCompleted = !todo.isCompleted,
                            updatedAt = System.currentTimeMillis().toString(),
                        )
                    todoDao.upsert(updated)
                    emit(AppResult.Success(updated.toExternalModel()))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }.catch { emit(AppResult.Error(it as Exception)) }

        override fun deleteTodo(id: String): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                todoDao.deleteById(id)
                emit(AppResult.Success(Unit))
            }.catch { emit(AppResult.Error(it as Exception)) }
    }

fun TodoEntity.toExternalModel() =
    Todo(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
