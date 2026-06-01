package com.aragabz.androidtemplate.core.network.mock

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Mock interceptor for development and testing.
 * Intercepts API calls and returns mock responses.
 * 
 * Set ENABLE_MOCK = true to use mock data instead of real API.
 */
class MockInterceptor : Interceptor {
    
    companion object {
        // Toggle this to enable/disable mocking
        const val ENABLE_MOCK = true
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!ENABLE_MOCK) {
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
        Thread.sleep(500)
        
        val json = """
        {
            "token": "mock-jwt-token-abc123xyz",
            "userId": "user-001",
            "name": "John Doe",
            "email": "john.doe@example.com"
        }
        """.trimIndent()
        
        return Response.Builder()
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }
    
    private fun mockRegisterResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(800)
        
        val json = """
        {
            "token": "mock-jwt-token-new-user-xyz789",
            "userId": "user-002",
            "name": "Jane Smith",
            "email": "jane.smith@example.com"
        }
        """.trimIndent()
        
        return Response.Builder()
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }
    
    private fun mockGetProfileResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(400)
        
        val json = """
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
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }
    
    private fun mockUpdateProfileResponse(request: okhttp3.Request): Response {
        // Simulate network delay
        Thread.sleep(600)
        
        // In a real scenario, you'd parse the request body and return it
        val json = """
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
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }
}
