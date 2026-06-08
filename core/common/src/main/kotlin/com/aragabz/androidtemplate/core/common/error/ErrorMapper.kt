package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.R
import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.ui.UiText

/**
 * Maps an [Exception] or [AppError] to a [UiText] for user display.
 */
fun Throwable.toUiText(): UiText {
    return when (this) {
        is AppError.NetworkError -> UiText.StringResource(R.string.error_network)
        is AppError.HttpError -> {
            when (code) {
                401 -> UiText.StringResource(R.string.error_http_unauthorized)
                404 -> UiText.StringResource(R.string.error_http_not_found)
                in 500..599 -> UiText.StringResource(R.string.error_http_server)
                else -> UiText.StringResource(R.string.error_http_unknown, code)
            }
        }
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
}
