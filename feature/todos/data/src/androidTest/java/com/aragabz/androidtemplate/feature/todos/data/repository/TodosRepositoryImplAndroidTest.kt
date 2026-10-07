package com.aragabz.androidtemplate.feature.todos.data.repository

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aragabz.androidtemplate.core.common.network.NetworkMonitor
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.CachePolicyStore
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.core.sync.manager.SyncManager
import com.aragabz.androidtemplate.feature.todos.data.local.dao.TodoDao
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodosRepositoryImplAndroidTest {
    private lateinit var database: TestTodosDatabase
    private lateinit var repository: TodosRepositoryImpl

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    TestTodosDatabase::class.java,
                ).allowMainThreadQueries()
                .build()

        repository =
            TodosRepositoryImpl(
                preferencesRepository = FakeUserPreferencesRepository(userId = "user-1"),
                networkMonitor = FakeNetworkMonitor(),
                syncManager = FakeSyncManager(),
                cachePolicyStore = InMemoryCachePolicyStore(),
                todoDao = database.todoDao(),
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getTodos_seedsDefaultTodosForActiveUser() =
        runBlocking {
            val result = repository.getTodos().first { it is AppResult.Success }

            val todos = (result as AppResult.Success).data
            assertEquals(2, todos.size)
            assertEquals(
                "user-1",
                database
                    .todoDao()
                    .getTodosByUserId("user-1")
                    .first()
                    .first()
                    .userId,
            )
        }
}

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class TestTodosDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

private class FakeUserPreferencesRepository(
    userId: String,
) : UserPreferencesRepository {
    private val state =
        MutableStateFlow(
            UserPreferences(
                userId = userId,
                authToken = null,
                theme = AppTheme.SYSTEM,
                language = "en",
            ),
        )

    override val userPreferences: Flow<UserPreferences> = state

    override suspend fun updateTheme(theme: AppTheme) {
        state.value = state.value.copy(theme = theme)
    }

    override suspend fun saveAuthToken(token: String) {
        state.value = state.value.copy(authToken = token)
    }

    override suspend fun saveUserId(userId: String) {
        state.value = state.value.copy(userId = userId)
    }

    override suspend fun updateLanguage(language: String) {
        state.value = state.value.copy(language = language)
    }

    override suspend fun clearSession() {
        state.value = state.value.copy(userId = null, authToken = null)
    }
}

private class FakeNetworkMonitor : NetworkMonitor {
    override val isOnline: Flow<Boolean> = flowOf(true)
}

private class FakeSyncManager : SyncManager {
    override fun schedulePeriodicSync(
        interval: Long,
        timeUnit: java.util.concurrent.TimeUnit,
    ) = Unit

    override fun triggerImmediateSync() = Unit

    override fun cancelAllSync() = Unit
}

private class InMemoryCachePolicyStore : CachePolicyStore {
    private val updatedByKey = mutableMapOf<String, Long>()
    private var lastCleanupRun: Long? = null

    override suspend fun touch(key: String) {
        updatedByKey[key] = System.currentTimeMillis()
    }

    override suspend fun readLastUpdated(key: String): Long? = updatedByKey[key]

    override suspend fun clear(key: String) {
        updatedByKey.remove(key)
    }

    override suspend fun markCleanupRun(nowMillis: Long) {
        lastCleanupRun = nowMillis
    }

    override suspend fun readLastCleanupRun(): Long? = lastCleanupRun
}
