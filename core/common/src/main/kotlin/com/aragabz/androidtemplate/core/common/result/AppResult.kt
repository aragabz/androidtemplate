package com.aragabz.androidtemplate.core.common.result

/**
 * A generic class that holds a value with its loading status.
 * @param T the type of data being held
 */
sealed class AppResult<out T> {
    /**
     * Success state with data.
     */
    data class Success<T>(val data: T) : AppResult<T>()

    /**
     * Error state with exception and optional message.
     */
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : AppResult<Nothing>()

    /**
     * Loading state.
     */
    data object Loading : AppResult<Nothing>()

    /**
     * Returns true if this is a Success result.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this is an Error result.
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this is a Loading result.
     */
    val isLoading: Boolean get() = this is Loading
}

/**
 * Returns the data if Success, null otherwise.
 */
fun <T> AppResult<T>.getOrNull(): T? = when (this) {
    is AppResult.Success -> data
    else -> null
}

/**
 * Returns the data if Success, or the provided default value otherwise.
 */
fun <T> AppResult<T>.getOrDefault(defaultValue: T): T = when (this) {
    is AppResult.Success -> data
    else -> defaultValue
}

/**
 * Maps the data if Success, returns the same Error/Loading otherwise.
 */
inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
    is AppResult.Loading -> this
}

/**
 * Executes the given block if Success.
 */
inline fun <T> AppResult<T>.onSuccess(block: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) {
        block(data)
    }
    return this
}

/**
 * Executes the given block if Error.
 */
inline fun <T> AppResult<T>.onError(block: (Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Error) {
        block(exception)
    }
    return this
}

/**
 * Executes the given block if Loading.
 */
inline fun <T> AppResult<T>.onLoading(block: () -> Unit): AppResult<T> {
    if (this is AppResult.Loading) {
        block()
    }
    return this
}
