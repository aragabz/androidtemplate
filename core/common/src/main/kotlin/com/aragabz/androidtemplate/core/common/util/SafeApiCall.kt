package com.aragabz.androidtemplate.core.common.util

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Executes the given API call block and wraps the result in AppResult.
 * Handles IOExceptions as NetworkError, other exceptions as UnknownError.
 *
 * @param dispatcher the coroutine dispatcher to use for the call
 * @param block the suspend function to execute
 * @return AppResult wrapping the success data or error
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    block: suspend () -> T
): AppResult<T> = withContext(dispatcher) {
    try {
        AppResult.Success(block())
    } catch (e: IOException) {
        AppResult.Error(AppError.NetworkError(e))
    } catch (e: Exception) {
        AppResult.Error(AppError.UnknownError(e))
    }
}
