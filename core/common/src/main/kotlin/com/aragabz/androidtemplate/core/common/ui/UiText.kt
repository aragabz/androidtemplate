package com.aragabz.androidtemplate.core.common.ui

import android.content.Context
import androidx.annotation.StringRes

/**
 * A sealed class to represent text that can be either a dynamic string or a string resource.
 * This is useful for passing text from the domain/data layer to the UI layer without
 * leaking Android dependencies like Context. Compose code resolves it with the `asString()` extension in core:ui.
 */
sealed class UiText {
    data class DynamicString(
        val value: String,
    ) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText() {
        // Value equality so UI states holding the same text compare equal.
        override fun equals(other: Any?): Boolean =
            other is StringResource && resId == other.resId && args.contentEquals(other.args)

        override fun hashCode(): Int = 31 * resId + args.contentHashCode()
    }

    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
}
