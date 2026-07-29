package com.aragabz.androidtemplate.core.datastore

interface CachePolicyStore {
    suspend fun touchTodosCache(
        userId: String,
        count: Int,
    )

    suspend fun readTodosCacheLastUpdated(userId: String): Long?

    suspend fun clearTodosCacheMetadata(userId: String)

    suspend fun markCleanupRun(nowMillis: Long)

    suspend fun readLastCleanupRun(): Long?
}
