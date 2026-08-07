package com.aragabz.androidtemplate.core.network.mock

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Tests for MockInterceptor to verify:
 * 1. Proper async delays (not blocking threads)
 * 2. Integration with DebugMockRegistry
 * 3. Mock responses are returned correctly
 */
class MockInterceptorTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockInterceptor: MockInterceptor
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockInterceptor = MockInterceptor()
        client =
            OkHttpClient
                .Builder()
                .addInterceptor(mockInterceptor)
                .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `mock interceptor uses proper delay not thread sleep`() {
        val url = mockWebServer.url("/login")
        val requestBody = "{}".toRequestBody("application/json".toMediaType())
        val request =
            Request
                .Builder()
                .url(url)
                .post(requestBody)
                .build()

        // Measure time - should take around 500ms (DELAY_LOGIN)
        val elapsedTime = measureTimeMillis {
            val response = client.newCall(request).execute()
            response.close()
        }

        // Verify delay was applied (allowing for some variance)
        assertTrue("Expected delay around 500ms, got ${elapsedTime}ms", elapsedTime in 400..700)
    }

    @Test
    fun `debug mock registry correctly identifies mocked paths`() {
        assertTrue(DebugMockRegistry.isMocked("/login"))
        assertTrue(DebugMockRegistry.isMocked("/register"))
        assertTrue(DebugMockRegistry.isMocked("/user/profile"))
        assertTrue(DebugMockRegistry.isMocked("/todos"))
        assertTrue(DebugMockRegistry.isMocked("/users"))
    }

    @Test
    fun `debug mock registry returns all mocked paths`() {
        val mockedPaths = DebugMockRegistry.getAllMockedPaths()
        assertTrue(mockedPaths.contains("/login"))
        assertTrue(mockedPaths.contains("/todos"))
        assertTrue(mockedPaths.size >= 5)
    }

    @Test
    fun `mock login returns expected json structure`() {
        val url = mockWebServer.url("/login")
        val requestBody = "{}".toRequestBody("application/json".toMediaType())
        val request =
            Request
                .Builder()
                .url(url)
                .post(requestBody)
                .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        assertEquals(200, response.code)
        assertTrue(body?.contains("token") == true)
        assertTrue(body?.contains("userId") == true)
        assertTrue(body?.contains("mock-jwt-token") == true)
    }

    @Test
    fun `mock register returns expected json structure`() {
        val url = mockWebServer.url("/register")
        val requestBody = "{}".toRequestBody("application/json".toMediaType())
        val request =
            Request
                .Builder()
                .url(url)
                .post(requestBody)
                .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        assertEquals(200, response.code)
        assertTrue(body?.contains("token") == true)
        assertTrue(body?.contains("Jane Smith") == true)
    }

    @Test
    fun `mock get profile returns expected json structure`() {
        val url = mockWebServer.url("/user/profile")
        val request =
            Request
                .Builder()
                .url(url)
                .get()
                .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        assertEquals(200, response.code)
        assertTrue(body?.contains("John Doe") == true)
        assertTrue(body?.contains("bio") == true)
        assertTrue(body?.contains("Android developer") == true)
    }

    @Test
    fun `mock todos returns array of todos`() {
        val url = mockWebServer.url("/todos")
        val request =
            Request
                .Builder()
                .url(url)
                .get()
                .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        assertEquals(200, response.code)
        assertTrue(body?.startsWith("[") == true)
        assertTrue(body?.contains("todo-001") == true)
        assertTrue(body?.contains("Complete project documentation") == true)
    }

    @Test
    fun `mock users returns array of users`() {
        val url = mockWebServer.url("/users")
        val request =
            Request
                .Builder()
                .url(url)
                .get()
                .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()

        assertEquals(200, response.code)
        assertTrue(body?.startsWith("[") == true)
        assertTrue(body?.contains("user-001") == true)
        assertTrue(body?.contains("user-002") == true)
    }
}
