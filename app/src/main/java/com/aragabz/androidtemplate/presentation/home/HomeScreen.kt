package com.aragabz.androidtemplate.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.navigation.navigateToDetails

@Composable
fun HomeScreen(navController: NavHostController) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = "Welcome to Android Template",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = spacing.small),
        )

        AppButton(
            text = "Go to Details",
            onClick = { navController.navigateToDetails("1") },
            variant = AppButtonVariant.PRIMARY,
            modifier = Modifier.padding(top = spacing.large),
        )
    }
}
