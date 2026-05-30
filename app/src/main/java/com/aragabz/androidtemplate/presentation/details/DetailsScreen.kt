package com.aragabz.androidtemplate.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.navigation.popBackStackSafely

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(id: String, navController: NavHostController) {
    val spacing = LocalSpacing.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStackSafely() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Details Screen",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Item ID: $id",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = spacing.small)
            )
            
            AppButton(
                text = "Go Back",
                onClick = { navController.popBackStackSafely() },
                variant = AppButtonVariant.SECONDARY,
                modifier = Modifier.padding(top = spacing.large)
            )
        }
    }
}
