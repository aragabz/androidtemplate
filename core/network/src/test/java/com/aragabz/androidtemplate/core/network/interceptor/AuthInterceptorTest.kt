package com.aragabz.androidtemplate.core.network.interceptor

import com.aragabz.androidtemplate.core.network.session.AuthTokenProvider
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {
    @Test
    fun `adds authorization header when token is available`() {
        val tokenProvider = mockk<AuthTokenProvider>()
        val chain = mockk<Interceptor.Chain>()
        val originalRequest = Request.Builder().url("https://example.com").build()

        every { tokenProvider.getAuthToken() } returns "token-123"
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } answers {
            val req = firstArg<Request>()
            responseFor(req)
        }

        val response = AuthInterceptor(tokenProvider).intercept(chain)

        assertEquals("Bearer token-123", response.request.header("Authorization"))
    }

    @Test
    fun `does not add authorization header when token is blank`() {
        val tokenProvider = mockk<AuthTokenProvider>()
        val chain = mockk<Interceptor.Chain>()
        val originalRequest = Request.Builder().url("https://example.com").build()

        every { tokenProvider.getAuthToken() } returns ""
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } answers {
            val req = firstArg<Request>()
            responseFor(req)
        }

        val response = AuthInterceptor(tokenProvider).intercept(chain)

        assertNull(response.request.header("Authorization"))
    }

    private fun responseFor(request: Request): Response =
        Response
            .Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()
}
