package com.aragabz.androidtemplate.feature.home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aragabz.androidtemplate.feature.home.R
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.navigation.Route

/**
 * Beautiful empty home dashboard screen placeholder for Vaulty.
 */
@Composable
fun HomeScreen(navController: NavHostController) {
    val spacing = LocalSpacing.current
    var isListView by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isListView) stringResource(id = R.string.home_view_mode_list) else stringResource(id = R.string.home_view_mode_dashboard),
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isListView,
                onCheckedChange = { isListView = it }
            )
        }

        Spacer(modifier = Modifier.height(spacing.medium))

        if (isListView) {
            ComponentsList(navController)
        } else {
            DashboardView()
        }
    }
}

@Composable
private fun DashboardView() {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(100.dp)
                    .background(
                        brush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surface,
                                    ),
                            ),
                        shape = RoundedCornerShape(50.dp),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(spacing.large))

        Text(
            text = stringResource(id = R.string.home_dashboard_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(spacing.small))

        Text(
            text = stringResource(id = R.string.home_dashboard_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = spacing.medium),
        )

        Spacer(modifier = Modifier.height(spacing.extraLarge))

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.home_welcome_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(spacing.extraSmall))
                Text(
                    text = stringResource(id = R.string.home_welcome_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ComponentsList(navController: NavHostController) {
    val spacing = LocalSpacing.current
    val items = listOf("Button Primary", "Button Secondary", "Button Ghost", "Button Destructive")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.home_navigation_examples),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = spacing.small)
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                AppButton(
                    text = stringResource(id = R.string.home_empty_screen),
                    onClick = { navController.navigate(Route.Empty) },
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = stringResource(id = R.string.home_error_screen),
                    onClick = { navController.navigate(Route.Error) },
                    variant = AppButtonVariant.DESTRUCTIVE,
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Default.Warning
                )
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
            Text(
                text = stringResource(id = R.string.home_component_examples),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = spacing.small)
            )
        }

        items(items) { item ->
            ListItem(
                headlineContent = { Text(item) },
                supportingContent = { Text(stringResource(id = R.string.home_component_example_template, item)) },
                trailingContent = {
                    val variant = when (item) {
                        "Button Primary" -> AppButtonVariant.PRIMARY
                        "Button Secondary" -> AppButtonVariant.SECONDARY
                        "Button Ghost" -> AppButtonVariant.GHOST
                        "Button Destructive" -> AppButtonVariant.DESTRUCTIVE
                        else -> AppButtonVariant.PRIMARY
                    }
                    AppButton(
                        text = stringResource(id = R.string.home_click),
                        onClick = { },
                        variant = variant
                    )
                }
            )
        }
    }
}
