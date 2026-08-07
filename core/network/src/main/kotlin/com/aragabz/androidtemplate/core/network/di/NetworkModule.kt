package com.aragabz.androidtemplate.core.network.di

import com.aragabz.androidtemplate.core.network.BuildConfig
import com.aragabz.androidtemplate.core.network.adapter.ApiResultCallAdapterFactory
import com.aragabz.androidtemplate.core.network.interceptor.AuthInterceptor
import com.aragabz.androidtemplate.core.network.interceptor.CachePolicyInterceptor
import com.aragabz.androidtemplate.core.network.interceptor.RetryInterceptor
import com.aragabz.androidtemplate.core.network.mock.MockInterceptor
import com.aragabz.androidtemplate.core.network.session.AuthTokenProvider
import com.aragabz.androidtemplate.core.network.session.DefaultAuthTokenProvider
import com.aragabz.androidtemplate.core.network.session.SessionManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing network dependencies: OkHttpClient, Retrofit, etc.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val NETWORK_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }

    @Provides
    @Singleton
    fun provideAuthTokenProvider(default: DefaultAuthTokenProvider): AuthTokenProvider = default

    @Provides
    @Singleton
    fun provideOkHttpClient(
        mockInterceptor: MockInterceptor,
        authInterceptor: AuthInterceptor,
        retryInterceptor: RetryInterceptor,
        cachePolicyInterceptor: CachePolicyInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            // Order: mock -> auth -> retry -> cache policy -> logging
            .addInterceptor(mockInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(cachePolicyInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            )
            // Uncomment and configure CertificatePinner for production
            // .certificatePinner(
            //     CertificatePinner.Builder()
            //         .add("your-api-domain.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            //         .build()
            // )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        sessionManager: SessionManager,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .addCallAdapterFactory(ApiResultCallAdapterFactory(sessionManager))
            .build()
    }

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL
}
