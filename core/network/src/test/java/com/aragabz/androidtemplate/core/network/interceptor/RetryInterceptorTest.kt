package com.aragabz.androidtemplate.core.network.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class RetryInterceptorTest {
    @Test
    fun `retries once for GET request on IOException`() {
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://example.com").get().build()

        every { chain.request() } returns request
        every { chain.proceed(request) } throws IOException("first failure") andThen responseFor(request)

        val response = RetryInterceptor().intercept(chain)

        assertEquals(200, response.code)
        verify(exactly = 2) { chain.proceed(request) }
    }

    @Test(expected = IOException::class)
    fun `does not retry non-GET request and propagates IOException`() {
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url("https://example.com").post("x".toRequestBody()).build()

        every { chain.request() } returns request
        every { chain.proceed(request) } throws IOException("post failure")

        RetryInterceptor().intercept(chain)
    }

    private fun responseFor(request: Request): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()
}
