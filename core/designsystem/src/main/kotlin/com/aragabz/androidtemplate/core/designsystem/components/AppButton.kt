package com.aragabz.androidtemplate.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class AppButtonVariant {
    PRIMARY, SECONDARY, GHOST, DESTRUCTIVE
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    
    when (variant) {
        AppButtonVariant.PRIMARY -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled && !isLoading,
            contentPadding = contentPadding
        ) {
            ButtonContent(text, isLoading, leadingIcon)
        }
        AppButtonVariant.SECONDARY -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled && !isLoading,
            contentPadding = contentPadding
        ) {
            ButtonContent(text, isLoading, leadingIcon)
        }
        AppButtonVariant.GHOST -> TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled && !isLoading,
            contentPadding = contentPadding
        ) {
            ButtonContent(text, isLoading, leadingIcon)
        }
        AppButtonVariant.DESTRUCTIVE -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            contentPadding = contentPadding
        ) {
            ButtonContent(text, isLoading, leadingIcon)
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    leadingIcon: ImageVector?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
