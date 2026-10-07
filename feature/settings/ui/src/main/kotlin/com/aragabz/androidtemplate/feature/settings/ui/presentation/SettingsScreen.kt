package com.aragabz.androidtemplate.feature.settings.ui.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import com.aragabz.androidtemplate.feature.settings.ui.R

/**
 * Settings tab: lists the settings sections and opens the one picked.
 */
@Composable
fun SettingsScreen(
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.onEvent(SettingsEvent.OnDismissError)
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        onThemeClick = onThemeClick,
        onLanguageClick = onLanguageClick,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    )
}

@Composable
internal fun SettingsScreenContent(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        Text(stringResource(id = R.string.settings_title), style = MaterialTheme.typography.headlineSmall)

        // Appearance section
        SectionHeader(text = stringResource(id = R.string.settings_section_appearance))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column {
                SettingsRow(
                    title = stringResource(id = R.string.settings_theme),
                    subtitle = uiState.selectedTheme.displayName(),
                    icon = {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(spacing.large),
                        )
                    },
                    onClick = onThemeClick,
                )
                SettingsRow(
                    title = stringResource(id = R.string.settings_language),
                    subtitle = uiState.selectedLanguage.displayName,
                    icon = {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(spacing.large),
                        )
                    },
                    onClick = onLanguageClick,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.medium))
        snackbarHost()
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = spacing.medium, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(spacing.medium))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ThemePreference.displayName(): String =
    when (this) {
        ThemePreference.SYSTEM -> stringResource(id = R.string.settings_theme_system)
        ThemePreference.LIGHT -> stringResource(id = R.string.settings_theme_light)
        ThemePreference.DARK -> stringResource(id = R.string.settings_theme_dark)
    }

@PreviewLightDark
@Composable
private fun SettingsScreenContentPreview() {
    AppTheme {
        SettingsScreenContent(uiState = SettingsUiState(selectedTheme = ThemePreference.DARK))
    }
}
