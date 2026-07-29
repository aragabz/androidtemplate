package com.aragabz.androidtemplate.core.designsystem.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Simple fade transition for content changes.
 */
@Composable
public fun <T> AnimatedFadeContent(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "fade_content",
    ) { state ->
        content(state)
    }
}

/**
 * Animated content with horizontal slide transition.
 * Useful for tab content, pager content, or step-by-step flows.
 *
 * @param targetState The target state to display (comparable for direction)
 * @param modifier Modifier for the container
 * @param content Content to display for each state
 */
@Composable
public fun <T : Comparable<T>> AnimatedSlideContent(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            if (targetState > initialState) {
                // Moving forward
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { it },
                ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { -it },
                    ) + fadeOut(animationSpec = tween(300))
            } else {
                // Moving backward
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { -it },
                ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { it },
                    ) + fadeOut(animationSpec = tween(300))
            }.using(SizeTransform(clip = false))
        },
        label = "slide_content",
    ) { state ->
        content(state)
    }
}

