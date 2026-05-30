package com.aragabz.androidtemplate.core.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing

/**
 * Full-screen error state with optional retry action.
 * Use when an error prevents showing any content.
 */
@Composable
fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    val spacing = LocalSpacing.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(spacing.medium))
        
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(spacing.small))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(spacing.large))
            AppButton(
                text = "Try Again",
                onClick = onRetry,
                variant = AppButtonVariant.PRIMARY
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ErrorScreenPreview() {
    AppTheme {
        ErrorScreen(
            message = "Unable to load data. Please check your connection and try again.",
            onRetry = { }
        )
    }
}
