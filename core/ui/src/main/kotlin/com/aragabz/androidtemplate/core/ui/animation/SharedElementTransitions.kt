package com.aragabz.androidtemplate.core.ui.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * CompositionLocal for [SharedTransitionScope].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

/**
 * CompositionLocal for [AnimatedVisibilityScope].
 */
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * A wrapper for [SharedTransitionLayout] that provides the [SharedTransitionScope]
 * to its children via [LocalSharedTransitionScope].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionWrapper(content: @Composable SharedTransitionScope.() -> Unit) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
        ) {
            content()
        }
    }
}

/**
 * Helper modifier to apply shared element transition using [LocalSharedTransitionScope]
 * and [LocalAnimatedVisibilityScope].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedElement(key: Any): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@sharedElement.sharedElement(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    } else {
        this
    }
}

/**
 * Helper modifier to apply shared bounds transition using [LocalSharedTransitionScope]
 * and [LocalAnimatedVisibilityScope].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedBounds(key: Any): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@sharedBounds.sharedBounds(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    } else {
        this
    }
}
