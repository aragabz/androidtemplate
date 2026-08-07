package com.aragabz.androidtemplate.core.network.interceptor

import com.aragabz.androidtemplate.core.network.session.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val tokenProvider: AuthTokenProvider,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = tokenProvider.getAuthToken()
            val request =
                if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }

            return chain.proceed(request)
        }
    }
