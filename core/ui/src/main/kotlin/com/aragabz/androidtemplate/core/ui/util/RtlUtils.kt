package com.aragabz.androidtemplate.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * A [Modifier] that mirrors the content horizontally if the layout direction is RTL.
 * Useful for icons that should point in the opposite direction (e.g., back arrows).
 */
@Composable
fun Modifier.mirrorRtl(): Modifier =
    if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
        this.scale(scaleX = -1f, scaleY = 1f)
    } else {
        this
    }
