package com.aragabz.androidtemplate.core.network.mock

import com.aragabz.androidtemplate.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

/**
 * Mock interceptor for development and testing.
 * Intercepts API calls and returns mock responses when enabled in BuildConfig.
 */
class MockInterceptor
    @Inject
    constructor() : Interceptor {
    companion object {
        private const val HTTP_OK = 200
        private const val DELAY_LOGIN = 500L
        private const val DELAY_REGISTER = 800L
        private const val DELAY_GET_PROFILE = 400L
        private const val DELAY_UPDATE_PROFILE = 600L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BuildConfig.DEBUG || !BuildConfig.ENABLE_MOCK_INTERCEPTOR) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        return when {
            // Auth endpoints
            path.endsWith("/login") && method == "POST" -> mockLoginResponse(request)
            path.endsWith("/register") && method == "POST" -> mockRegisterResponse(request)

            // User endpoints
            path.contains("/user/profile") && method == "GET" -> mockGetProfileResponse(request)
            path.contains("/user/profile") && method == "PUT" -> mockUpdateProfileResponse(request)

            // Not mocked - proceed with real request
            else -> chain.proceed(request)
        }
    }

    private fun mockLoginResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(DELAY_LOGIN)

        val json =
            """
            {
                "token": "mock-jwt-token-abc123xyz",
                "userId": "user-001",
                "name": "John Doe",
                "email": "john.doe@example.com"
            }
            """.trimIndent()

        return Response.Builder()
            .code(HTTP_OK)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }

    private fun mockRegisterResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(DELAY_REGISTER)

        val json =
            """
            {
                "token": "mock-jwt-token-new-user-xyz789",
                "userId": "user-002",
                "name": "Jane Smith",
                "email": "jane.smith@example.com"
            }
            """.trimIndent()

        return Response.Builder()
            .code(HTTP_OK)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }

    private fun mockGetProfileResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(DELAY_GET_PROFILE)

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

        return Response.Builder()
            .code(HTTP_OK)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }

    private fun mockUpdateProfileResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(DELAY_UPDATE_PROFILE)

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

        return Response.Builder()
            .code(HTTP_OK)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }
}
