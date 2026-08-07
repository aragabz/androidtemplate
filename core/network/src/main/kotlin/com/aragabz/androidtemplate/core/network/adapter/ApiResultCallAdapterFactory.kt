package com.aragabz.androidtemplate.core.network.adapter

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.network.session.SessionManager
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * CallAdapter.Factory that wraps Retrofit responses in AppResult.
 * Automatically handles HTTP errors, network errors, and 401 unauthorized responses.
 */
class ApiResultCallAdapterFactory(
    private val sessionManager: SessionManager,
) : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        val isCall = getRawType(returnType) == Call::class.java && returnType is ParameterizedType
        if (!isCall) return null

        val upperBound = getParameterUpperBound(0, returnType)
        val isAppResult = getRawType(upperBound) == AppResult::class.java && upperBound is ParameterizedType

        return if (isAppResult) {
            val bodyType = getParameterUpperBound(0, upperBound)
            ApiResultCallAdapter<Any>(bodyType, sessionManager)
        } else {
            null
        }
    }
}

private class ApiResultCallAdapter<T>(
    private val successType: Type,
    private val sessionManager: SessionManager,
) : CallAdapter<T, Call<AppResult<T>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<AppResult<T>> = AppResultCall(call, sessionManager)
}

private class AppResultCall<T>(
    private val delegate: Call<T>,
    private val sessionManager: SessionManager,
) : Call<AppResult<T>> {
    override fun enqueue(callback: Callback<AppResult<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    val result: AppResult<T> =
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                AppResult.Success(body)
                            } else {
                                AppResult.Error(
                                    AppError.UnknownError(Throwable("Response body is null")),
                                )
                            }
                        } else {
                            val code = response.code()

                            // Handle 401 Unauthorized - notify SessionManager
                            // Actual handling (logout, navigation) happens in lifecycle-scoped collectors
                            if (code == HTTP_UNAUTHORIZED) {
                                sessionManager.notifyUnauthorized()
                            }

                            AppResult.Error(
                                AppError.HttpError(
                                    code = code,
                                    message = response.message() ?: "HTTP Error $code",
                                ),
                            )
                        }
                    callback.onResponse(this@AppResultCall, Response.success(result))
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    val result: AppResult<T> =
                        if (t is IOException) {
                            AppResult.Error(AppError.NetworkError(t))
                        } else {
                            AppResult.Error(AppError.UnknownError(t))
                        }
                    callback.onResponse(this@AppResultCall, Response.success(result))
                }
            },
        )
    }

    override fun execute(): Response<AppResult<T>> =
        throw UnsupportedOperationException("AppResultCall does not support execute()")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun clone(): Call<AppResult<T>> = AppResultCall(delegate.clone(), sessionManager)

    companion object {
        private const val HTTP_UNAUTHORIZED = 401
    }
}
