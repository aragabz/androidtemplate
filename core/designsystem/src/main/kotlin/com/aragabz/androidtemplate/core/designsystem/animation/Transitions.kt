package com.aragabz.androidtemplate.core.designsystem.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Standard enter/exit transitions used across the app.
 */
object Transitions {
    /**
     * Fade in transition.
     */
    val fadeIn: EnterTransition = fadeIn(animationSpec = tween(300))

    /**
     * Fade out transition.
     */
    val fadeOut: ExitTransition = fadeOut(animationSpec = tween(300))

    /**
     * Slide in from left + fade in.
     */
    val slideInFromLeft: EnterTransition = slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { -it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide in from right + fade in.
     */
    val slideInFromRight: EnterTransition = slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide in from top + fade in.
     */
    val slideInFromTop: EnterTransition = slideInVertically(
        animationSpec = tween(300),
        initialOffsetY = { -it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide in from bottom + fade in.
     */
    val slideInFromBottom: EnterTransition = slideInVertically(
        animationSpec = tween(300),
        initialOffsetY = { it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide out to left + fade out.
     */
    val slideOutToLeft: ExitTransition = slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { -it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Slide out to right + fade out.
     */
    val slideOutToRight: ExitTransition = slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Slide out to top + fade out.
     */
    val slideOutToTop: ExitTransition = slideOutVertically(
        animationSpec = tween(300),
        targetOffsetY = { -it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Slide out to bottom + fade out.
     */
    val slideOutToBottom: ExitTransition = slideOutVertically(
        animationSpec = tween(300),
        targetOffsetY = { it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Expand vertically from top + fade in.
     */
    val expandFromTop: EnterTransition = expandVertically(
        animationSpec = tween(300),
        expandFrom = Alignment.Top,
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Expand vertically from center + fade in.
     */
    val expandFromCenter: EnterTransition = expandVertically(
        animationSpec = tween(300),
        expandFrom = Alignment.CenterVertically,
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Shrink vertically to top + fade out.
     */
    val shrinkToTop: ExitTransition = shrinkVertically(
        animationSpec = tween(300),
        shrinkTowards = Alignment.Top,
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Shrink vertically to center + fade out.
     */
    val shrinkToCenter: ExitTransition = shrinkVertically(
        animationSpec = tween(300),
        shrinkTowards = Alignment.CenterVertically,
    ) + fadeOut(animationSpec = tween(300))
}

/**
 * Animated visibility wrapper with fade in/out transitions.
 *
 * @param visible Whether the content should be visible
 * @param modifier Modifier for the animated content
 * @param content Content to show/hide with animation
 */
@Composable
fun AnimatedFade(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = Transitions.fadeIn,
        exit = Transitions.fadeOut,
        content = content,
    )
}

/**
 * Animated visibility wrapper with slide from left transition.
 *
 * @param visible Whether the content should be visible
 * @param modifier Modifier for the animated content
 * @param content Content to show/hide with animation
 */
@Composable
fun AnimatedSlideLeft(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = Transitions.slideInFromLeft,
        exit = Transitions.slideOutToLeft,
        content = content,
    )
}

/**
 * Animated visibility wrapper with slide from right transition.
 *
 * @param visible Whether the content should be visible
 * @param modifier Modifier for the animated content
 * @param content Content to show/hide with animation
 */
@Composable
fun AnimatedSlideRight(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = Transitions.slideInFromRight,
        exit = Transitions.slideOutToRight,
        content = content,
    )
}

/**
 * Animated visibility wrapper with expand/collapse transition.
 *
 * @param visible Whether the content should be visible
 * @param modifier Modifier for the animated content
 * @param content Content to show/hide with animation
 */
@Composable
fun AnimatedExpand(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = Transitions.expandFromCenter,
        exit = Transitions.shrinkToCenter,
        content = content,
    )
}
