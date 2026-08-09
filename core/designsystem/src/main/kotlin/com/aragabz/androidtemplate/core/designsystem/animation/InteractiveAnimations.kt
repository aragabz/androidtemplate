package com.aragabz.androidtemplate.core.designsystem.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Animates scale on press (tap + hold effect).
 * Scales down while pressed, scales back up on release.
 *
 * @param pressedScale Scale value when pressed (default 0.95)
 * @param animationSpec Animation spec for the scale animation
 */
fun Modifier.pressAnimation(
    pressedScale: Float = 0.95f,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.fast,
): Modifier =
    composed {
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) pressedScale else 1f,
            animationSpec = animationSpec,
            label = "press_scale",
        )

        this
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                )
            }
    }

/**
 * Animates elevation on press (lift effect).
 * Increases elevation while pressed, decreases on release.
 *
 * @param pressed Whether the element is pressed
 * @param normalElevation Elevation when not pressed
 * @param pressedElevation Elevation when pressed
 * @param animationSpec Animation spec for the elevation animation
 */
@Composable
fun animateElevationAsState(
    pressed: Boolean,
    normalElevation: Dp = 2.dp,
    pressedElevation: Dp = 8.dp,
    animationSpec: AnimationSpec<Dp> = AnimationSpecs.fastDp,
): Dp =
    animateDpAsState(
        targetValue = if (pressed) pressedElevation else normalElevation,
        animationSpec = animationSpec,
        label = "elevation",
    ).value

/**
 * Shake animation modifier for error states or attention grabbing.
 *
 * @param enabled Whether the shake animation is enabled
 * @param shakeDistance Maximum horizontal shake distance in pixels
 */
fun Modifier.shakeAnimation(
    enabled: Boolean,
    shakeDistance: Float = 10f,
): Modifier =
    composed {
        val offsetX = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(enabled) {
            if (enabled) {
                scope.launch {
                    // Shake pattern: right -> left -> right -> center
                    offsetX.animateTo(shakeDistance, AnimationSpecs.fast)
                    offsetX.animateTo(-shakeDistance, AnimationSpecs.fast)
                    offsetX.animateTo(shakeDistance / 2, AnimationSpecs.fast)
                    offsetX.animateTo(0f, AnimationSpecs.fast)
                }
            }
        }

        graphicsLayer {
            translationX = offsetX.value
        }
    }

/**
 * Bounce animation modifier for emphasis or success states.
 *
 * @param enabled Whether the bounce animation is enabled
 * @param bounceScale Maximum scale during bounce
 */
fun Modifier.bounceAnimation(
    enabled: Boolean,
    bounceScale: Float = 1.2f,
): Modifier =
    composed {
        val scale = remember { Animatable(1f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(enabled) {
            if (enabled) {
                scope.launch {
                    scale.animateTo(bounceScale, AnimationSpecs.springMedium)
                    scale.animateTo(1f, AnimationSpecs.springMedium)
                }
            }
        }

        scale(scale.value)
    }

/**
 * Rotation animation modifier.
 *
 * @param enabled Whether to rotate
 * @param degrees Target rotation in degrees
 * @param animationSpec Animation spec for rotation
 */
fun Modifier.rotateAnimation(
    enabled: Boolean,
    degrees: Float = 180f,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.standard,
): Modifier =
    composed {
        val rotation by animateFloatAsState(
            targetValue = if (enabled) degrees else 0f,
            animationSpec = animationSpec,
            label = "rotation",
        )

        graphicsLayer {
            rotationZ = rotation
        }
    }
