package com.aragabz.androidtemplate.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colors for success, warning, and info states.
 * These colors are separate from Material3's built-in error colors
 * and provide consistent visual feedback across the app.
 */
@Immutable
data class SemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
)

/**
 * Light theme semantic colors.
 */
val LightSemanticColors =
    SemanticColors(
        success = SuccessLight,
        onSuccess = OnSuccessLight,
        successContainer = SuccessContainerLight,
        onSuccessContainer = OnSuccessContainerLight,
        warning = WarningLight,
        onWarning = OnWarningLight,
        warningContainer = WarningContainerLight,
        onWarningContainer = OnWarningContainerLight,
        info = InfoLight,
        onInfo = OnInfoLight,
        infoContainer = InfoContainerLight,
        onInfoContainer = OnInfoContainerLight,
    )

/**
 * Dark theme semantic colors.
 */
val DarkSemanticColors =
    SemanticColors(
        success = SuccessDark,
        onSuccess = OnSuccessDark,
        successContainer = SuccessContainerDark,
        onSuccessContainer = OnSuccessContainerDark,
        warning = WarningDark,
        onWarning = OnWarningDark,
        warningContainer = WarningContainerDark,
        onWarningContainer = OnWarningContainerDark,
        info = InfoDark,
        onInfo = OnInfoDark,
        infoContainer = InfoContainerDark,
        onInfoContainer = OnInfoContainerDark,
    )

/**
 * CompositionLocal for accessing semantic colors in composables.
 *
 * Usage:
 * ```
 * val semanticColors = LocalSemanticColors.current
 * Icon(
 *     imageVector = Icons.Default.CheckCircle,
 *     tint = semanticColors.success
 * )
 * ```
 */
val LocalSemanticColors =
    staticCompositionLocalOf {
        LightSemanticColors
    }
