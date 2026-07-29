package com.aragabz.androidtemplate.feature.home.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aragabz.androidtemplate.core.navigation.Route

/**
 * Splash screen displaying "Home" with a smooth scaling animation.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec =
                tween(
                    durationMillis = 1000,
                ),
        )
    }

    LaunchedEffect(state) {
        when (state) {
            SplashViewModel.SplashState.NavigateToAuth -> {
                navController.navigate(Route.Auth) {
                    popUpTo(Route.Splash) { inclusive = true }
                }
            }
            SplashViewModel.SplashState.NavigateToMain -> {
                navController.navigate(Route.Main) {
                    popUpTo(Route.Splash) { inclusive = true }
                }
            }
            SplashViewModel.SplashState.Loading -> { /* Do nothing */ }
        }
    }

    SplashScreenContent(scale = scale.value)
}

@Composable
internal fun SplashScreenContent(
    scale: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    ),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Android Template",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scale),
        )
    }
}
