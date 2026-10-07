package com.aragabz.androidtemplate.core.datastore

/**
 * Persists when cached data was last refreshed, so repositories can decide whether a cache is stale.
 * Each feature picks its own [key] (e.g. "todos_<userId>").
 */
interface CachePolicyStore {
    /** Records that the cache identified by [key] was refreshed now. */
    suspend fun touch(key: String)

    /** Epoch millis of the last refresh of [key], or null if it was never refreshed. */
    suspend fun readLastUpdated(key: String): Long?

    suspend fun clear(key: String)

    suspend fun markCleanupRun(nowMillis: Long)

    suspend fun readLastCleanupRun(): Long?
}
