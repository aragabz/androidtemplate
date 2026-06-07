package com.aragabz.androidtemplate.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.common.network.NetworkMonitor
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing

/**
 * Banner that appears at the top when device is offline.
 * Automatically observes NetworkMonitor and shows/hides.
 */
@Composable
fun NetworkBanner(
    networkMonitor: NetworkMonitor,
    modifier: Modifier = Modifier,
) {
    val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)

    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        OfflineBanner(modifier = modifier)
    }
}

/**
 * Static offline banner for preview/manual control.
 */
@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color(0xFFFFA726)) // Amber
                .padding(spacing.small),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing.small),
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Offline",
                tint = Color.White,
            )
            Text(
                text = "You're offline",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NetworkBannerPreview() {
    AppTheme {
        OfflineBanner()
    }
}
