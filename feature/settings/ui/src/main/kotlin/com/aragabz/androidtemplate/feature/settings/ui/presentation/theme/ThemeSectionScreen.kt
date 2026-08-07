package com.aragabz.androidtemplate.feature.settings.ui.presentation.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import com.aragabz.androidtemplate.feature.settings.ui.R
import com.aragabz.androidtemplate.feature.settings.ui.presentation.SettingsEvent
import com.aragabz.androidtemplate.feature.settings.ui.presentation.SettingsViewModel

/**
 * Theme settings section - pick between System / Light / Dark.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSectionScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_theme)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.settings_back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        ThemeSectionContent(
            selectedTheme = uiState.selectedTheme,
            isLoading = uiState.isLoading,
            onThemeSelected = { theme ->
                viewModel.onEvent(SettingsEvent.OnThemeSelected(theme))
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
internal fun ThemeSectionContent(
    selectedTheme: ThemePreference,
    isLoading: Boolean,
    onThemeSelected: (ThemePreference) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
    ) {
        Text(
            text = stringResource(id = R.string.settings_theme_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.padding(spacing.medium))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column {
                ThemePreference.entries.forEachIndexed { index, theme ->
                    ThemeRow(
                        theme = theme,
                        isSelected = selectedTheme == theme,
                        enabled = !isLoading,
                        onClick = { onThemeSelected(theme) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeRow(
    theme: ThemePreference,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = spacing.medium, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = theme.displayName(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ThemePreference.displayName(): String =
    when (this) {
        ThemePreference.SYSTEM -> stringResource(id = R.string.settings_theme_system)
        ThemePreference.LIGHT -> stringResource(id = R.string.settings_theme_light)
        ThemePreference.DARK -> stringResource(id = R.string.settings_theme_dark)
    }
