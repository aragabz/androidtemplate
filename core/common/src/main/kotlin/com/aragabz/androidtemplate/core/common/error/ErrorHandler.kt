package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Wraps a suspend function call with error handling.
 * Automatically converts exceptions to AppResult.Error.
 *
 * @param dispatcher Optional coroutine dispatcher for execution context (defaults to current context)
 * @param block The suspend function to execute
 * @return AppResult wrapping the success data or error
 *
 * Usage in repositories:
 * ```
 * // Simple case (uses current context)
 * suspend fun getUser(id: String): AppResult<User> = withErrorHandling {
 *     api.getUser(id)
 * }
 *
 * // With explicit dispatcher
 * suspend fun getUser(id: String): AppResult<User> = withErrorHandling(Dispatchers.IO) {
 *     api.getUser(id)
 * }
 * ```
 */
suspend fun <T> withErrorHandling(
    dispatcher: CoroutineDispatcher? = null,
    block: suspend () -> T,
): AppResult<T> =
    runSuspendCatching {
        if (dispatcher != null) withContext(dispatcher) { block() } else block()
    }.fold(
        onSuccess = { AppResult.Success(it) },
        onFailure = { AppResult.Error(it.toAppError()) },
    )

/**
 * Like [runCatching], but for suspending code: rethrows [CancellationException] so coroutine cancellation
 * keeps propagating instead of being reported as a failure. Only [Exception]s are captured.
 */
inline fun <T> runSuspendCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

/**
 * Wraps a Flow emission with error handling.
 * Automatically converts exceptions to AppResult.Error.
 *
 * Usage in repositories:
 * ```
 * fun observeUsers(): Flow<AppResult<List<User>>> = flow {
 *     database.observeUsers().collect { emit(it) }
 * }.catchAsAppError()
 * ```
 */
fun <T> Flow<T>.catchAsAppError(): Flow<AppResult<T>> =
    this
        .map<T, AppResult<T>> { AppResult.Success(it) }
        .catch { emit(AppResult.Error(it.toAppError())) }

/**
 * Creates a validation error with field context.
 */
fun validationError(
    message: String,
    field: String? = null,
): AppError.ValidationError = AppError.ValidationError(message, field)

/**
 * Creates an authentication error with specific reason.
 */
fun authError(
    message: String,
    reason: com.aragabz.androidtemplate.core.common.result.AuthErrorReason =
        com.aragabz.androidtemplate.core.common.result.AuthErrorReason.UNKNOWN,
): AppError.AuthError = AppError.AuthError(message, reason)

/**
 * Creates a database error with operation context.
 */
fun databaseError(
    cause: Throwable,
    operation: String? = null,
): AppError.DatabaseError = AppError.DatabaseError(cause, operation)
