package com.aragabz.androidtemplate.core.network.mock

/**
 * Debug-only registry that can be expanded to map endpoint paths to local payloads.
 */
object DebugMockRegistry {
    private val mockedPaths = setOf(
        "/todos",
        "/users",
    )

    fun isMocked(path: String): Boolean = mockedPaths.contains(path)
}
