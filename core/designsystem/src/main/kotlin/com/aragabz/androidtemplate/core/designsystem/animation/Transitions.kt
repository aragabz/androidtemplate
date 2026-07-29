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
public object Transitions {
    /**
     * Fade in transition.
     */
    public val fadeIn: EnterTransition = fadeIn(animationSpec = tween(300))

    /**
     * Fade out transition.
     */
    public val fadeOut: ExitTransition = fadeOut(animationSpec = tween(300))

    /**
     * Slide in from left + fade in.
     */
    public val slideInFromLeft: EnterTransition = slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { -it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide in from right + fade in.
     */
    public val slideInFromRight: EnterTransition = slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide in from top + fade in.
     */
    public val slideInFromTop: EnterTransition = slideInVertically(
        animationSpec = tween(300),
        initialOffsetY = { -it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide in from bottom + fade in.
     */
    public val slideInFromBottom: EnterTransition = slideInVertically(
        animationSpec = tween(300),
        initialOffsetY = { it },
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Slide out to left + fade out.
     */
    public val slideOutToLeft: ExitTransition = slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { -it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Slide out to right + fade out.
     */
    public val slideOutToRight: ExitTransition = slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Slide out to top + fade out.
     */
    public val slideOutToTop: ExitTransition = slideOutVertically(
        animationSpec = tween(300),
        targetOffsetY = { -it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Slide out to bottom + fade out.
     */
    public val slideOutToBottom: ExitTransition = slideOutVertically(
        animationSpec = tween(300),
        targetOffsetY = { it },
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Expand vertically from top + fade in.
     */
    public val expandFromTop: EnterTransition = expandVertically(
        animationSpec = tween(300),
        expandFrom = Alignment.Top,
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Expand vertically from center + fade in.
     */
    public val expandFromCenter: EnterTransition = expandVertically(
        animationSpec = tween(300),
        expandFrom = Alignment.CenterVertically,
    ) + fadeIn(animationSpec = tween(300))

    /**
     * Shrink vertically to top + fade out.
     */
    public val shrinkToTop: ExitTransition = shrinkVertically(
        animationSpec = tween(300),
        shrinkTowards = Alignment.Top,
    ) + fadeOut(animationSpec = tween(300))

    /**
     * Shrink vertically to center + fade out.
     */
    public val shrinkToCenter: ExitTransition = shrinkVertically(
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
public fun AnimatedFade(
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
public fun AnimatedSlideLeft(
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
public fun AnimatedSlideRight(
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
public fun AnimatedExpand(
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
