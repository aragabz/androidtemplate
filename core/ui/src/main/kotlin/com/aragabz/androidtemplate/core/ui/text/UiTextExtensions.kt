package com.aragabz.androidtemplate.core.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Resolves [UiText] in composition.
 */
@Composable
fun UiText.asString(): String =
    when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> stringResource(resId, *args)
    }
