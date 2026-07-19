package com.aragabz.androidtemplate.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Context.cachePolicyDataStore by preferencesDataStore(name = "cache_policy_store")

@Singleton
class CachePolicyStoreImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : CachePolicyStore {
        override suspend fun touchTodosCache(userId: String, count: Int) {
            val nowMillis = System.currentTimeMillis()
            context.cachePolicyDataStore.edit { prefs ->
                prefs[userScopedKey(userId)] = nowMillis
                prefs[userScopedCountKey(userId)] = count.toLong()
            }
        }

        override suspend fun readTodosCacheLastUpdated(userId: String): Long? {
            val prefs = context.cachePolicyDataStore.data.first()
            return prefs[userScopedKey(userId)]
        }

        override suspend fun clearTodosCacheMetadata(userId: String) {
            context.cachePolicyDataStore.edit { prefs ->
                prefs.remove(userScopedKey(userId))
                prefs.remove(userScopedCountKey(userId))
            }
        }

        override suspend fun markCleanupRun(nowMillis: Long) {
            context.cachePolicyDataStore.edit { prefs ->
                prefs[lastCleanupRunKey] = nowMillis
            }
        }

        override suspend fun readLastCleanupRun(): Long? {
            val prefs = context.cachePolicyDataStore.data.first()
            return prefs[lastCleanupRunKey]
        }

        private fun userScopedKey(userId: String) = longPreferencesKey("todos_cache_last_updated_$userId")

        private fun userScopedCountKey(userId: String) = longPreferencesKey("todos_cache_count_$userId")

        private companion object {
            val lastCleanupRunKey = longPreferencesKey("cache_cleanup_last_run")
        }
    }
