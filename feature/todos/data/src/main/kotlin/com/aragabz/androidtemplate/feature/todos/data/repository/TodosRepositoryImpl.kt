package com.aragabz.androidtemplate.feature.todos.data.repository

import com.aragabz.androidtemplate.core.common.network.NetworkMonitor
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.CachePolicyStore
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.sync.manager.SyncManager
import com.aragabz.androidtemplate.feature.todos.data.local.dao.TodoDao
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation backed by Room with in-memory fallback for transient failures.
 */
@Singleton
class TodosRepositoryImpl
    @Inject
    constructor(
        private val preferencesRepository: UserPreferencesRepository,
        private val networkMonitor: NetworkMonitor,
        private val syncManager: SyncManager,
        private val cachePolicyStore: CachePolicyStore,
        private val todoDao: TodoDao,
    ) : TodosRepository {
        private val inMemoryCache = mutableMapOf<String, List<Todo>>()

        override fun getTodos(): Flow<AppResult<List<Todo>>> =
            flow {
                emit(AppResult.Loading)

                val userId = getActiveUserId()
                val isOnline = networkMonitor.isOnline.first()

                if (isOnline) {
                    // Trigger refresh attempt when connectivity is available.
                    syncManager.triggerImmediateSync()
                }

                val currentTodos = todoDao.getTodosByUserId(userId).first()
                if (currentTodos.isEmpty() && isOnline) {
                    addDefaultTodos(userId)
                } else if (currentTodos.isNotEmpty()) {
                    val mapped = currentTodos.map { it.toExternalModel() }
                    inMemoryCache[userId] = mapped
                    cachePolicyStore.touch(cacheKey(userId))
                }

                if (!isOnline && isCacheExpired(userId)) {
                    inMemoryCache[userId]?.let { emit(AppResult.Success(it)) }
                }

                emitAll(
                    todoDao
                        .getTodosByUserId(userId)
                        .map { entities ->
                            val mapped = entities.map { it.toExternalModel() }
                            inMemoryCache[userId] = mapped
                            cachePolicyStore.touch(cacheKey(userId))
                            AppResult.Success(mapped)
                        }.catch { emit(AppResult.Error(it as Exception)) },
                )
            }.catch {
                val userId = runCatching { getActiveUserId() }.getOrDefault("default")
                val fallback = inMemoryCache[userId]
                if (fallback != null) {
                    emit(AppResult.Success(fallback))
                } else {
                    emit(AppResult.Error(it as Exception))
                }
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
                val now = System.currentTimeMillis()
                val newTodo =
                    TodoEntity(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = title,
                        description = description,
                        isCompleted = false,
                        createdAt = now,
                        updatedAt = null,
                    )
                todoDao.upsert(newTodo)
                cachePolicyStore.touch(cacheKey(userId))
                emit(AppResult.Success(newTodo.toExternalModel()))
            }.catch { emit(AppResult.Error(it as Exception)) }

        override fun toggleTodo(id: String): Flow<AppResult<Todo>> =
            flow {
                emit(AppResult.Loading)
                val todo = todoDao.getTodoById(id)
                if (todo != null) {
                    val now = System.currentTimeMillis()
                    val updated =
                        todo.copy(
                            isCompleted = !todo.isCompleted,
                            updatedAt = now,
                        )
                    todoDao.upsert(updated)
                    cachePolicyStore.touch(cacheKey(updated.userId))
                    emit(AppResult.Success(updated.toExternalModel()))
                } else {
                    emit(AppResult.Error(Exception("Todo not found")))
                }
            }.catch { emit(AppResult.Error(it as Exception)) }

        override fun deleteTodo(id: String): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                val userId = getActiveUserId()
                todoDao.deleteById(id)
                cachePolicyStore.touch(cacheKey(userId))
                emit(AppResult.Success(Unit))
            }.catch { emit(AppResult.Error(it as Exception)) }

        private suspend fun getActiveUserId(): String =
            preferencesRepository.userPreferences.first().userId ?: "default"

        private suspend fun addDefaultTodos(userId: String) {
            val baseTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7) // 7 days ago
            val defaults =
                listOf(
                    TodoEntity(
                        id = "1",
                        userId = userId,
                        title = "Welcome to Android Template!",
                        description = "This is your first todo item in this account.",
                        isCompleted = false,
                        createdAt = baseTime,
                        updatedAt = null,
                    ),
                    TodoEntity(
                        id = "2",
                        userId = userId,
                        title = "Add a new account",
                        description = "Go to settings and create a new account to test switching.",
                        isCompleted = false,
                        createdAt = baseTime + TimeUnit.HOURS.toMillis(1), // 1 hour after first
                        updatedAt = null,
                    ),
                )
            todoDao.upsertAll(defaults)
        }

        private suspend fun isCacheExpired(userId: String): Boolean {
            val lastUpdated = cachePolicyStore.readLastUpdated(cacheKey(userId)) ?: return true
            return (System.currentTimeMillis() - lastUpdated) > TimeUnit.HOURS.toMillis(CACHE_RETENTION_HOURS)
        }

        private fun cacheKey(userId: String) = "todos_$userId"

        private companion object {
            const val CACHE_RETENTION_HOURS = 24L
        }
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
