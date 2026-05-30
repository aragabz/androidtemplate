package com.aragabz.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.navigation.AppNavigationHost
import com.aragabz.androidtemplate.presentation.details.DetailsScreen
import com.aragabz.androidtemplate.presentation.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity - single activity architecture with Navigation 3.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigationHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        homeScreen = { nav -> HomeScreen(nav) },
                        detailsScreen = { id, nav -> DetailsScreen(id, nav) }
                    )
                }
            }
        }
    }
}
