package com.aragabz.androidtemplate.feature.todos.data.repository

import com.aragabz.androidtemplate.core.common.network.NetworkMonitor
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.CachePolicyStore
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.core.sync.manager.SyncManager
import com.aragabz.androidtemplate.core.testing.FakeUserPreferencesRepository
import com.aragabz.androidtemplate.feature.todos.data.local.dao.TodoDao
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class TodosRepositoryImplTest {
    private val preferences = FakeUserPreferencesRepository(UserPreferences(userId = "alice"))
    private val todoDao = FakeTodoDao()
    private val repository =
        TodosRepositoryImpl(
            preferencesRepository = preferences,
            networkMonitor = FakeNetworkMonitor(online = true),
            syncManager = NoOpSyncManager(),
            cachePolicyStore = InMemoryCachePolicyStore(),
            todoDao = todoDao,
        )

    @Test
    fun `seeding a second user keeps the first user's seeded todos`() =
        runTest {
            val aliceTodos = repository.firstTodos()
            preferences.preferences.value = UserPreferences(userId = "bob")
            val bobTodos = repository.firstTodos()

            assertEquals(2, aliceTodos.size)
            assertEquals(2, bobTodos.size)
            assertEquals(
                2,
                todoDao.rows.value.values
                    .count { it.userId == "alice" },
            )
            assertEquals(
                2,
                todoDao.rows.value.values
                    .count { it.userId == "bob" },
            )
            assertTrue(aliceTodos.map { it.id }.intersect(bobTodos.map { it.id }.toSet()).isEmpty())
        }

    @Test
    fun `seeding runs once per user`() =
        runTest {
            repository.firstTodos()
            repository.firstTodos()

            assertEquals(
                2,
                todoDao.rows.value.values
                    .count { it.userId == "alice" },
            )
        }

    @Test
    fun `getTodoById maps a non-Exception throwable to Error`() =
        runTest {
            val failure = AssertionError("dao exploded")
            todoDao.getTodoByIdFailure = failure

            val result = repository.getTodoById("any").first { it !is AppResult.Loading }

            assertTrue(result is AppResult.Error)
            assertSame(failure, (result as AppResult.Error).exception)
        }

    private suspend fun TodosRepositoryImpl.firstTodos() =
        (getTodos().first { it is AppResult.Success && it.data.isNotEmpty() } as AppResult.Success).data

    private class FakeTodoDao : TodoDao {
        val rows = MutableStateFlow<Map<String, TodoEntity>>(emptyMap())
        var getTodoByIdFailure: Throwable? = null

        override fun getTodosByUserId(userId: String): Flow<List<TodoEntity>> =
            rows.map { all -> all.values.filter { it.userId == userId } }

        override suspend fun getTodoById(id: String): TodoEntity? {
            getTodoByIdFailure?.let { throw it }
            return rows.value[id]
        }

        override suspend fun deleteById(id: String) {
            rows.value -= id
        }

        override suspend fun upsert(entity: TodoEntity) {
            rows.value += entity.id to entity
        }

        override suspend fun upsertAll(entities: List<TodoEntity>) {
            rows.value += entities.associateBy { it.id }
        }

        override suspend fun delete(entity: TodoEntity) {
            rows.value -= entity.id
        }
    }

    private class FakeNetworkMonitor(
        online: Boolean,
    ) : NetworkMonitor {
        override val isOnline: Flow<Boolean> = flowOf(online)
    }

    private class NoOpSyncManager : SyncManager {
        override fun schedulePeriodicSync(
            interval: Long,
            timeUnit: TimeUnit,
        ) = Unit

        override fun triggerImmediateSync() = Unit

        override fun cancelAllSync() = Unit
    }

    private class InMemoryCachePolicyStore : CachePolicyStore {
        private val lastUpdated = mutableMapOf<String, Long>()
        private var lastCleanup: Long? = null

        override suspend fun touch(key: String) {
            lastUpdated[key] = System.currentTimeMillis()
        }

        override suspend fun readLastUpdated(key: String): Long? = lastUpdated[key]

        override suspend fun clear(key: String) {
            lastUpdated.remove(key)
        }

        override suspend fun markCleanupRun(nowMillis: Long) {
            lastCleanup = nowMillis
        }

        override suspend fun readLastCleanupRun(): Long? = lastCleanup
    }
}
