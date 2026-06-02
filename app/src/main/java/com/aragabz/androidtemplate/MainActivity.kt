package com.aragabz.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity — single activity architecture.
 * Navigation flow: Splash → Auth / Main.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
