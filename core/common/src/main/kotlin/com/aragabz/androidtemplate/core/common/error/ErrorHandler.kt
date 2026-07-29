package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.ui.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Wraps a suspend function call with error handling.
 * Automatically converts exceptions to AppResult.Error.
 *
 * Usage in repositories:
 * ```
 * suspend fun getUser(id: String): AppResult<User> = withErrorHandling {
 *     api.getUser(id) // Throws exceptions
 * }
 * ```
 */
suspend fun <T> withErrorHandling(block: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(e.toAppError())
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
 * Extension to convert Flow<AppResult<T>> error states to UiText.
 * Useful for ViewModels that need to display errors.
 *
 * Usage in ViewModels:
 * ```
 * val users: StateFlow<AppResult<List<User>>> = repository
 *     .observeUsers()
 *     .stateIn(viewModelScope, SharingStarted.Lazily, AppResult.Loading)
 *
 * val errorMessage: StateFlow<UiText?> = users
 *     .errorAsUiText()
 *     .stateIn(viewModelScope, SharingStarted.Lazily, null)
 * ```
 */
fun <T> Flow<AppResult<T>>.errorAsUiText(): Flow<UiText?> =
    this.map { result ->
        result.errorUiText
    }

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
