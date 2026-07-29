package com.aragabz.androidtemplate.core.designsystem.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp

/**
 * Standard animation specifications used across the app.
 * Provides consistent animation timing and easing.
 */
public object AnimationSpecs {
    /**
     * Fast animation for quick UI feedback (150ms).
     * Use for: Button presses, ripples, toggles.
     */
    public val fast: FiniteAnimationSpec<Float> = tween(durationMillis = 150)

    /**
     * Standard animation for most UI transitions (300ms).
     * Use for: Screen transitions, expanding/collapsing, visibility changes.
     */
    public val standard: FiniteAnimationSpec<Float> = tween(durationMillis = 300)

    /**
     * Slow animation for emphasis (500ms).
     * Use for: Loading states, important state changes, onboarding.
     */
    public val slow: FiniteAnimationSpec<Float> = tween(durationMillis = 500)

    /**
     * Fast animation spec for Dp values.
     */
    public val fastDp: FiniteAnimationSpec<Dp> = tween(durationMillis = 150)

    /**
     * Standard animation spec for Dp values.
     */
    public val standardDp: FiniteAnimationSpec<Dp> = tween(durationMillis = 300)

    /**
     * Spring animation with medium stiffness.
     * Use for: Interactive elements, bouncy effects, draggable items.
     */
    public val springMedium: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /**
     * Spring animation with low stiffness for smooth, slow bounces.
     * Use for: Smooth dragging, gentle emphasis.
     */
    public val springLow: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
    )

    /**
     * Spring animation with high stiffness for quick, snappy effects.
     * Use for: Quick interactions, snapping to position.
     */
    public val springHigh: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    /**
     * No bounce spring for smooth, natural motion.
     * Use for: Smooth scrolling, page transitions.
     */
    public val springNoBounce: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    )
}
