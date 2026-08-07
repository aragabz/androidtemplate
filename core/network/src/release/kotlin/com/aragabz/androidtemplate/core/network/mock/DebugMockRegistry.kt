package com.aragabz.androidtemplate.core.network.mock

/**
 * Release stub for DebugMockRegistry.
 * In release builds, no mocking is performed.
 */
object DebugMockRegistry {
    /**
     * Always returns false in release builds - no mocking.
     */
    fun isMocked(path: String): Boolean = false

    /**
     * Returns empty set in release builds.
     */
    fun getAllMockedPaths(): Set<String> = emptySet()

    /**
     * No-op in release builds.
     */
    fun enableMockingFor(path: String) {}

    /**
     * No-op in release builds.
     */
    fun disableMockingFor(path: String) {}

    /**
     * No-op in release builds.
     */
    fun clearRuntimeMocks() {}
}
