package com.aragabz.androidtemplate.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class CachePolicyInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder = request.newBuilder()

            if (request.method == "GET" && request.header("Cache-Control").isNullOrBlank()) {
                requestBuilder.header("Cache-Control", "public, max-age=60")
            }

            val response = chain.proceed(requestBuilder.build())

            return if (request.method == "GET") {
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=60, stale-while-revalidate=300")
                    .build()
            } else {
                response
            }
        }
    }
