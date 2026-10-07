package com.aragabz.androidtemplate.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cachePolicyDataStore by preferencesDataStore(name = "cache_policy_store")

@Singleton
class CachePolicyStoreImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : CachePolicyStore {
        override suspend fun touch(key: String) {
            val nowMillis = System.currentTimeMillis()
            context.cachePolicyDataStore.edit { prefs ->
                prefs[lastUpdatedKey(key)] = nowMillis
            }
        }

        override suspend fun readLastUpdated(key: String): Long? {
            val prefs = context.cachePolicyDataStore.data.first()
            return prefs[lastUpdatedKey(key)]
        }

        override suspend fun clear(key: String) {
            context.cachePolicyDataStore.edit { prefs ->
                prefs.remove(lastUpdatedKey(key))
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

        private fun lastUpdatedKey(key: String) = longPreferencesKey("cache_last_updated_$key")

        private companion object {
            val lastCleanupRunKey = longPreferencesKey("cache_cleanup_last_run")
        }
    }
