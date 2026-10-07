package com.aragabz.androidtemplate

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.navigation.AppNavGraph
import com.aragabz.androidtemplate.navigation.navigateToSignIn
import dagger.hilt.android.AndroidEntryPoint
import com.aragabz.androidtemplate.core.datastore.model.AppTheme as ThemePreference

/**
 * Main activity — single activity architecture.
 * App state (start destination, theme) comes from [MainViewModel]; the native splash stays up until it is ready.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { viewModel.uiState.value is MainUiState.Loading }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Apply language changes made in settings.
            LaunchedEffect(Unit) {
                viewModel.languageChanges.collect { language ->
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
                }
            }

            val state = uiState
            if (state is MainUiState.Ready) {
                val darkTheme =
                    when (state.theme) {
                        ThemePreference.LIGHT -> false
                        ThemePreference.DARK -> true
                        ThemePreference.SYSTEM -> isSystemInDarkTheme()
                    }

                AppTheme(darkTheme = darkTheme) {
                    val navController = rememberNavController()

                    LaunchedEffect(state.isSignInRequired) {
                        if (state.isSignInRequired) {
                            navController.navigateToSignIn()
                            viewModel.onSignInShown()
                        }
                    }

                    AppNavGraph(
                        navController = navController,
                        startDestination = state.startDestination,
                        onSignOut = viewModel::signOut,
                    )
                }
            }
        }
    }
}
