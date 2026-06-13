package com.aragabz.androidtemplate.feature.todos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.aragabz.androidtemplate.core.database.dao.BaseDao
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for todos.
 */
@Dao
interface TodoDao : BaseDao<TodoEntity> {
    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTodosByUserId(userId: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    suspend fun getTodoById(id: String): TodoEntity?

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: String)
}
