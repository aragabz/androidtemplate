package com.aragabz.androidtemplate.feature.profile.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.LoadingScreen
import com.aragabz.androidtemplate.feature.profile.ui.R

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.onEvent(ProfileEvent.OnDismissError)
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    )
}

@Composable
internal fun ProfileScreenContent(
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
) {
    val spacing = LocalSpacing.current

    when {
        uiState.isLoading -> LoadingScreen(message = stringResource(id = R.string.profile_loading), modifier = modifier)
        uiState.profile == null -> {
            EmptyScreen(
                message = stringResource(id = R.string.profile_empty_message),
                subtitle = stringResource(id = R.string.profile_empty_subtitle),
                modifier = modifier,
            )
        }
        else -> {
            val profile = uiState.profile
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                Text(text = stringResource(id = R.string.profile_title), style = MaterialTheme.typography.headlineSmall)

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(spacing.medium)) {
                        Text(stringResource(id = R.string.profile_user_id, profile?.userId.orEmpty()))
                        Text(stringResource(id = R.string.profile_name, profile?.displayName.orEmpty()))
                        Text(stringResource(id = R.string.profile_email, profile?.email.orEmpty()))
                    }
                }

                AppButton(
                    text = stringResource(id = R.string.profile_refresh),
                    onClick = { onEvent(ProfileEvent.OnRefresh) },
                    variant = AppButtonVariant.SECONDARY,
                    modifier = Modifier.fillMaxWidth(),
                )

                AppButton(
                    text = stringResource(id = R.string.profile_sign_out),
                    onClick = { onEvent(ProfileEvent.OnSignOut) },
                    variant = AppButtonVariant.DESTRUCTIVE,
                    modifier = Modifier.fillMaxWidth(),
                )

                snackbarHost()
            }
        }
    }
}
