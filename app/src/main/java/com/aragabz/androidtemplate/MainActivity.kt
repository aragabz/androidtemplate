package com.aragabz.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.aragabz.androidtemplate.core.datastore.model.AppTheme as ThemePreference

/**
 * Main activity — single activity architecture.
 * Navigation flow: Auth / Main (determined by auth status).
 * Observes 401 unauthorized events via MainViewModel and navigates to login when needed.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getAuthSessionUseCase: GetAuthSessionUseCase

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    private val viewModel: MainViewModel by viewModels()

    private var isAuthenticated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        var keepOnScreen = true
        val splashScreen = installSplashScreen()

        // Keep the native splash on screen while the authentication status
        // is being checked, then dismiss it.
        splashScreen.setKeepOnScreenCondition { keepOnScreen }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val preferences by userPreferencesRepository.userPreferences.collectAsState(
                initial = UserPreferences(),
            )

            val darkTheme =
                when (preferences.theme) {
                    ThemePreference.LIGHT -> false
                    ThemePreference.DARK -> true
                    ThemePreference.SYSTEM -> isSystemInDarkTheme()
                }

            AppTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                var authReady by remember { mutableStateOf(false) }

                // Check authentication status, then dismiss the native splash.
                LaunchedEffect(Unit) {
                    isAuthenticated = getAuthSessionUseCase().first().isAuthenticated
                    authReady = true
                    keepOnScreen = false
                }

                if (authReady) {
                    // Observe 401 unauthorized events and navigate to login
                    // This is lifecycle-scoped via LaunchedEffect
                    LaunchedEffect(Unit) {
                        viewModel.navigateToLogin.collect {
                            // Clear back stack and navigate to Auth screen
                            navController.navigate(Route.Auth) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    AppNavGraph(
                        navController = navController,
                        startDestination = if (isAuthenticated) Route.Main else Route.Auth,
                    )
                }
            }
        }
    }
}
