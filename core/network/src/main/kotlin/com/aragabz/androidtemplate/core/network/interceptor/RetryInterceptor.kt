package com.aragabz.androidtemplate.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class RetryInterceptor
    @Inject
    constructor() : Interceptor {
        companion object {
            private const val MAX_RETRIES = 1
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var lastError: IOException? = null

            if (request.method != "GET") {
                return chain.proceed(request)
            }

            repeat(MAX_RETRIES + 1) {
                try {
                    return chain.proceed(request)
                } catch (e: IOException) {
                    lastError = e
                }
            }

            throw lastError ?: IOException("Request failed with unknown IO error")
        }
    }
