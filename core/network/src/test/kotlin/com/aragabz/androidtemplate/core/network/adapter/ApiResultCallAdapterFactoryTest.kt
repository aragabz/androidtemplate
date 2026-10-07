package com.aragabz.androidtemplate.core.network.adapter

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.network.session.SessionManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET

class ApiResultCallAdapterFactoryTest {
    private interface TestApi {
        @DELETE("item")
        suspend fun deleteItem(): AppResult<Unit>

        @GET("name")
        suspend fun getName(): AppResult<String>
    }

    private lateinit var server: MockWebServer
    private lateinit var api: TestApi

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        api =
            Retrofit
                .Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(ApiResultCallAdapterFactory(SessionManager()))
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(TestApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `204 No Content for a Unit endpoint is Success`() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(204))

            assertEquals(AppResult.Success(Unit), api.deleteItem())
        }

    @Test
    fun `200 with a body for a Unit endpoint is Success`() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

            assertEquals(AppResult.Success(Unit), api.deleteItem())
        }

    @Test
    fun `204 No Content for an endpoint expecting a body is still an Error`() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(204))

            val result = api.getName()

            assertTrue(result is AppResult.Error && result.exception is AppError.UnknownError)
        }

    @Test
    fun `200 with a body is Success`() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(200).setBody("\"Ada\""))

            assertEquals(AppResult.Success("Ada"), api.getName())
        }
}
