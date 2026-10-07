package com.aragabz.androidtemplate.core.ui.text

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.result.AuthErrorReason
import com.aragabz.androidtemplate.core.ui.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Maps an [Exception] or [AppError] to a [UiText] for user display.
 */
fun Throwable.toUiText(): UiText =
    when (this) {
        is AppError.NetworkError -> UiText.StringResource(R.string.error_network)

        is AppError.TimeoutError -> UiText.StringResource(R.string.error_timeout)

        is AppError.HttpError -> {
            when (code) {
                400 -> UiText.StringResource(R.string.error_http_validation)
                401 -> UiText.StringResource(R.string.error_http_unauthorized)
                403 -> UiText.StringResource(R.string.error_http_forbidden)
                404 -> UiText.StringResource(R.string.error_http_not_found)
                409 -> UiText.StringResource(R.string.error_http_conflict)
                503 -> UiText.StringResource(R.string.error_http_service_unavailable)
                in 500..599 -> UiText.StringResource(R.string.error_http_server)
                else -> UiText.StringResource(R.string.error_http_unknown, code)
            }
        }

        is AppError.DatabaseError -> {
            operation?.let {
                UiText.StringResource(R.string.error_database_operation, it)
            } ?: UiText.StringResource(R.string.error_database)
        }

        is AppError.ValidationError -> {
            field?.let {
                UiText.StringResource(R.string.error_validation_field, it)
            } ?: UiText.StringResource(R.string.error_validation)
        }

        is AppError.AuthError -> {
            when (reason) {
                AuthErrorReason.INVALID_CREDENTIALS ->
                    UiText.StringResource(R.string.error_auth_invalid_credentials)

                AuthErrorReason.TOKEN_EXPIRED ->
                    UiText.StringResource(R.string.error_auth_token_expired)

                AuthErrorReason.SESSION_EXPIRED ->
                    UiText.StringResource(R.string.error_auth_session_expired)

                AuthErrorReason.UNAUTHORIZED ->
                    UiText.StringResource(R.string.error_auth_unauthorized)

                AuthErrorReason.UNKNOWN ->
                    UiText.StringResource(R.string.error_auth_unknown)
            }
        }

        is AppError.ParsingError -> UiText.StringResource(R.string.error_parsing)

        is AppError.UnknownError -> UiText.StringResource(R.string.error_unknown)

        else -> {
            val message = this.localizedMessage ?: this.message
            if (message != null) {
                UiText.DynamicString(message)
            } else {
                UiText.StringResource(R.string.error_unknown)
            }
        }
    }

/**
 * Returns the error as [UiText] if this is an Error result, null otherwise.
 */
val AppResult<*>.errorUiText: UiText?
    get() = (this as? AppResult.Error)?.exception?.toUiText()

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
