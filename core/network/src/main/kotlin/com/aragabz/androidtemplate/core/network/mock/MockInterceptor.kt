package com.aragabz.androidtemplate.core.network.mock

import com.aragabz.androidtemplate.core.network.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

/**
 * Mock interceptor for development and testing.
 * Intercepts API calls and returns mock responses when enabled in BuildConfig.
 *
 * Uses proper coroutine delays instead of Thread.sleep() and integrates with DebugMockRegistry.
 */
class MockInterceptor
    @Inject
    constructor() : Interceptor {
        companion object {
            private const val HTTP_OK = 200
            private const val HTTP_NOT_FOUND = 404
            private const val DELAY_LOGIN = 500L
            private const val DELAY_REGISTER = 800L
            private const val DELAY_GET_PROFILE = 400L
            private const val DELAY_UPDATE_PROFILE = 600L
            private const val DEFAULT_DELAY = 300L
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            if (!BuildConfig.DEBUG || !BuildConfig.ENABLE_MOCK_INTERCEPTOR) {
                return chain.proceed(chain.request())
            }

            val request = chain.request()
            val path = request.url.encodedPath
            val method = request.method

            // Check if this path should be mocked
            if (!DebugMockRegistry.isMocked(path)) {
                return chain.proceed(request)
            }

            return when {
                // Auth endpoints
                path.endsWith("/login") && method == "POST" -> mockLoginResponse(request)
                path.endsWith("/register") && method == "POST" -> mockRegisterResponse(request)
                path.endsWith("/logout") && method == "POST" -> mockLogoutResponse(request)

                // User endpoints
                path.contains("/user/profile") && method == "GET" -> mockGetProfileResponse(request)
                path.contains("/user/profile") && method == "PUT" -> mockUpdateProfileResponse(request)

                // Profile endpoints (new)
                path.matches(Regex(".*/users/[^/]+$")) && method == "GET" -> mockGetUserByIdResponse(request)

                // Todos endpoint
                path.endsWith("/todos") && method == "GET" -> mockGetTodosResponse(request)

                // Users endpoint
                path.endsWith("/users") && method == "GET" -> mockGetUsersResponse(request)

                // Not mocked - proceed with real request
                else -> chain.proceed(request)
            }
        }

        private fun mockLoginResponse(request: okhttp3.Request): Response {
            // Use proper coroutine delay instead of Thread.sleep()
            runBlocking { delay(DELAY_LOGIN) }

            val json =
                """
                {
                    "token": "mock-jwt-token-abc123xyz",
                    "userId": "user-001",
                    "name": "John Doe",
                    "email": "john.doe@example.com"
                }
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockRegisterResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DELAY_REGISTER) }

            val json =
                """
                {
                    "token": "mock-jwt-token-new-user-xyz789",
                    "userId": "user-002",
                    "name": "Jane Smith",
                    "email": "jane.smith@example.com"
                }
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockGetProfileResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DELAY_GET_PROFILE) }

            val json =
                """
                {
                    "id": "user-001",
                    "name": "John Doe",
                    "email": "john.doe@example.com",
                    "bio": "Android developer passionate about Kotlin and Jetpack Compose. Building awesome mobile experiences! 🚀",
                    "avatarUrl": null,
                    "createdAt": "2024-01-15T10:30:00Z"
                }
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockUpdateProfileResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DELAY_UPDATE_PROFILE) }

            // In a real scenario, you'd parse the request body and return it
            val json =
                """
                {
                    "id": "user-001",
                    "name": "John Doe Updated",
                    "email": "john.updated@example.com",
                    "bio": "Updated bio text",
                    "avatarUrl": null,
                    "createdAt": "2024-01-15T10:30:00Z"
                }
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockGetTodosResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DEFAULT_DELAY) }

            val json =
                """
                [
                    {
                        "id": "todo-001",
                        "title": "Complete project documentation",
                        "description": "Write comprehensive docs for the new feature",
                        "completed": false,
                        "createdAt": "2024-07-28T10:00:00Z"
                    },
                    {
                        "id": "todo-002",
                        "title": "Code review",
                        "description": "Review PR #123",
                        "completed": true,
                        "createdAt": "2024-07-27T14:30:00Z"
                    }
                ]
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockGetUsersResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DEFAULT_DELAY) }

            val json =
                """
                [
                    {
                        "id": "user-001",
                        "name": "John Doe",
                        "email": "john.doe@example.com"
                    },
                    {
                        "id": "user-002",
                        "name": "Jane Smith",
                        "email": "jane.smith@example.com"
                    }
                ]
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockGetUserByIdResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DEFAULT_DELAY) }

            // Extract user ID from path (e.g., /users/user-001)
            val userId = request.url.pathSegments.last()

            val json =
                """
                {
                    "id": "$userId",
                    "name": "John Doe",
                    "email": "john.doe@example.com"
                }
                """.trimIndent()

            return buildJsonResponse(request, json, HTTP_OK)
        }

        private fun mockLogoutResponse(request: okhttp3.Request): Response {
            runBlocking { delay(DEFAULT_DELAY) }

            // Logout typically returns empty response with 200 OK
            return buildJsonResponse(request, "{}", HTTP_OK)
        }

        /**
         * Helper to build a JSON response with proper OkHttp structure.
         */
        private fun buildJsonResponse(
            request: okhttp3.Request,
            json: String,
            code: Int = HTTP_OK,
        ): Response =
            Response
                .Builder()
                .code(code)
                .message(if (code == HTTP_OK) "OK" else "Not Found")
                .body(json.toResponseBody("application/json".toMediaType()))
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .build()
    }
