package com.aragabz.androidtemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.aragabz.androidtemplate.core.ui.theme.AppTheme
import com.aragabz.androidtemplate.navigation.NavigationHost
import com.aragabz.androidtemplate.navigation.rememberNavigationManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity - single activity architecture.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navigationManager = rememberNavigationManager()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationHost(
                        navigationManager = navigationManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
