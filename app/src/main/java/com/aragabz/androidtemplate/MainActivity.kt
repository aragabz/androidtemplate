package com.aragabz.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main activity — single activity architecture.
 * Navigation flow: Auth / Main (determined by auth status).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getAuthSessionUseCase: GetAuthSessionUseCase

    private var isAuthenticated: Boolean = false
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // Keep splash screen visible while checking auth status
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)

        // Check authentication status
        lifecycleScope.launch {
            isAuthenticated = getAuthSessionUseCase().first().isAuthenticated
            keepSplashScreen = false
        }

        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    startDestination = if (isAuthenticated) Route.Main else Route.Auth,
                )
            }
        }
    }
}
