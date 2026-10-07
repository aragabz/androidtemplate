package com.aragabz.androidtemplate.feature.auth.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.feature.auth.ui.R

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnSignedIn by rememberUpdatedState(onSignedIn)

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.onEvent(AuthEvent.OnDismissError)
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            currentOnSignedIn()
        }
    }

    AuthScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            // Title
            Text(
                text =
                    stringResource(
                        if (uiState.isSignUpMode) {
                            R.string.auth_title_sign_up
                        } else {
                            R.string.auth_title_sign_in
                        },
                    ),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            // Subtitle
            Text(
                text =
                    stringResource(
                        if (uiState.isSignUpMode) {
                            R.string.auth_subtitle_sign_up
                        } else {
                            R.string.auth_subtitle_sign_in
                        },
                    ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            AuthCredentialFields(
                uiState = uiState,
                onEvent = onEvent,
                onDone = {
                    keyboardController?.hide()
                    onEvent(AuthEvent.OnSubmit)
                },
            )

            Spacer(modifier = Modifier.height(spacing.small))

            // Submit button
            AppButton(
                text =
                    stringResource(
                        if (uiState.isSignUpMode) {
                            R.string.auth_sign_up
                        } else {
                            R.string.auth_sign_in
                        },
                    ),
                onClick = {
                    keyboardController?.hide()
                    onEvent(AuthEvent.OnSubmit)
                },
                isLoading = uiState.isLoading,
                enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank(),
                variant = AppButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
            )

            // Toggle mode button
            TextButton(
                onClick = { onEvent(AuthEvent.OnToggleMode) },
                enabled = !uiState.isLoading,
            ) {
                Text(
                    text =
                        stringResource(
                            if (uiState.isSignUpMode) {
                                R.string.auth_toggle_sign_in
                            } else {
                                R.string.auth_toggle_sign_up
                            },
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))
        }

        // Snackbar at bottom
        Box(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            snackbarHost()
        }
    }
}

@Composable
private fun AuthCredentialFields(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    onDone: () -> Unit,
) {
    val passwordFocusRequester = remember { FocusRequester() }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Email field
    OutlinedTextField(
        value = uiState.email,
        onValueChange = { onEvent(AuthEvent.OnEmailChanged(it)) },
        label = { Text(stringResource(id = R.string.auth_email)) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !uiState.isLoading,
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        keyboardActions =
            KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() },
            ),
        isError = uiState.emailError != null,
        supportingText =
            uiState.emailError?.let {
                { Text(it.asString(LocalContext.current)) }
            },
    )

    // Password field
    OutlinedTextField(
        value = uiState.password,
        onValueChange = { onEvent(AuthEvent.OnPasswordChanged(it)) },
        label = { Text(stringResource(id = R.string.auth_password)) },
        modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
        enabled = !uiState.isLoading,
        singleLine = true,
        visualTransformation =
            if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector =
                        if (passwordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                    contentDescription =
                        stringResource(
                            if (passwordVisible) R.string.auth_hide_password else R.string.auth_show_password,
                        ),
                )
            }
        },
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = { onDone() },
            ),
        isError = uiState.passwordError != null,
        supportingText =
            uiState.passwordError?.let {
                { Text(it.asString(LocalContext.current)) }
            },
    )
}

@PreviewLightDark
@Composable
private fun AuthScreenContentPreview() {
    AppTheme {
        AuthScreenContent(
            uiState = AuthUiState(email = "ada@example.com"),
            onEvent = {},
        )
    }
}
