package com.aragabz.androidtemplate.core.network.mock

/**
 * Debug-only registry that maps endpoint paths to indicate which should be mocked.
 *
 * This registry is consulted by MockInterceptor to determine if a request should
 * return a mock response or proceed to the real network.
 *
 * Usage:
 * - Add paths here to enable mocking for those endpoints
 * - Remove paths to allow real network calls
 * - Check [isMocked] to determine if a path should be intercepted
 */
object DebugMockRegistry {
    /**
     * Set of endpoint paths that should be mocked in debug builds.
     * Paths should match the encoded path from the request URL.
     */
    private val mockedPaths =
        setOf(
            // Auth endpoints
            "/login",
            "/register",
            "/logout",
            // User endpoints
            "/user/profile",
            // Todos endpoint
            "/todos",
            // Users endpoint
            "/users",
        )

    /**
     * Checks if the given path should be mocked.
     *
     * @param path The encoded path from the request URL
     * @return true if this path should return a mock response, false otherwise
     */
    fun isMocked(path: String): Boolean = mockedPaths.any { path.contains(it) }

    /**
     * Returns all registered mock paths for debugging/logging.
     */
    fun getAllMockedPaths(): Set<String> = mockedPaths
}
