package com.aragabz.androidtemplate.feature.settings.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.feature.settings.domain.model.ThemePreference
import com.aragabz.androidtemplate.feature.settings.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.onEvent(SettingsEvent.OnDismissError)
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreenContent(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        Text(stringResource(id = R.string.settings_title), style = MaterialTheme.typography.headlineSmall)

        Text(stringResource(id = R.string.settings_theme), style = MaterialTheme.typography.titleMedium)
        ThemePreference.entries.forEach { theme ->
            AppButton(
                text = if (uiState.selectedTheme == theme) stringResource(id = R.string.settings_theme_selected, theme.name) else theme.name,
                onClick = { onEvent(SettingsEvent.OnThemeSelected(theme)) },
                enabled = !uiState.isLoading,
                variant = if (uiState.selectedTheme == theme) AppButtonVariant.PRIMARY else AppButtonVariant.SECONDARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Text(stringResource(id = R.string.settings_language), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = uiState.selectedLanguage,
            onValueChange = { onEvent(SettingsEvent.OnLanguageSelected(it)) },
            label = { Text(stringResource(id = R.string.settings_language_code)) },
            singleLine = true,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        snackbarHost()
    }
}
