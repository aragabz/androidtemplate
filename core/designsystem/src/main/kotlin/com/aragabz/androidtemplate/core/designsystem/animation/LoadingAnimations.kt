package com.aragabz.androidtemplate.core.designsystem.animation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Shimmer effect modifier for loading states.
 *
 * @param colors Colors for the shimmer gradient; defaults to the theme's surface colors
 * @param durationMillis Duration of one shimmer animation cycle
 */
fun Modifier.shimmerEffect(
    colors: List<Color>? = null,
    durationMillis: Int = 1000,
): Modifier =
    composed {
        val colorScheme = MaterialTheme.colorScheme
        val gradientColors =
            colors ?: listOf(colorScheme.surfaceVariant, colorScheme.surface, colorScheme.surfaceVariant)
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis),
                repeatMode = RepeatMode.Restart,
            ),
            label = "shimmer_translate",
        )

        background(
            brush = Brush.linearGradient(
                colors = gradientColors,
                start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                end = Offset(translateAnim, translateAnim),
            ),
        )
    }

/**
 * Pulsing animation for emphasis.
 *
 * @param minAlpha Minimum alpha value
 * @param maxAlpha Maximum alpha value
 * @param durationMillis Duration of one pulse cycle
 */
@Composable
fun rememberPulsingAlpha(
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 1f,
    durationMillis: Int = 1000,
): Float {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_alpha",
    )
    return alpha
}

/**
 * Rotating animation value for loading spinners.
 *
 * @param durationMillis Duration of one complete rotation
 */
@Composable
fun rememberRotatingAngle(durationMillis: Int = 1000): Float {
    val transition = rememberInfiniteTransition(label = "rotate")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotate_angle",
    )
    return angle
}
