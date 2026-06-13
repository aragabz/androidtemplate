package com.aragabz.androidtemplate.core.database.dao

import androidx.room.Delete
import androidx.room.Upsert

/**
 * Base DAO interface providing common database operations.
 * All DAOs should extend this interface.
 *
 * @param T the entity type
 */
interface BaseDao<T> {
    /**
     * Insert or update an entity. If the entity exists, it will be updated.
     */
    @Upsert
    suspend fun upsert(entity: T)

    /**
     * Insert or update multiple entities.
     */
    @Upsert
    suspend fun upsertAll(entities: List<T>)

    /**
     * Delete an entity from the database.
     */
    @Delete
    suspend fun delete(entity: T)
}
