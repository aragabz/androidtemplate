package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.aragabz.androidtemplate.feature.auth.ui.R

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.onEvent(AuthEvent.OnDismissError)
        }
    }

    AuthScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHost = { androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) },
    )
}

@Composable
internal fun AuthScreenContent(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
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
        Text(
            text = stringResource(id = R.string.auth_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        Text(
            text =
                if (uiState.isAuthenticated) {
                    stringResource(id = R.string.auth_signed_in_as, uiState.currentUserId.orEmpty())
                } else {
                    stringResource(id = R.string.auth_sign_in_hint)
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = uiState.userIdInput,
            onValueChange = { onEvent(AuthEvent.OnUserIdChanged(it)) },
            label = { Text(stringResource(id = R.string.auth_user_id)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && !uiState.isAuthenticated,
            singleLine = true,
        )

        if (uiState.isAuthenticated) {
            AppButton(
                text = stringResource(id = R.string.auth_sign_out),
                onClick = { onEvent(AuthEvent.OnSignOut) },
                isLoading = uiState.isLoading,
                variant = AppButtonVariant.DESTRUCTIVE,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            AppButton(
                text = stringResource(id = R.string.auth_sign_in),
                onClick = { onEvent(AuthEvent.OnSignIn) },
                isLoading = uiState.isLoading,
                enabled = uiState.userIdInput.isNotBlank(),
                variant = AppButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(spacing.small))
        snackbarHost()
    }
}
