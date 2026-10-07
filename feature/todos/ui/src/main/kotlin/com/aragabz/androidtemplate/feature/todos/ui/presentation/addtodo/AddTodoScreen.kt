package com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.ui.text.asString
import com.aragabz.androidtemplate.feature.todos.ui.R

/**
 * Add todo screen: collects [AddTodoViewModel] state and leaves once the todo is saved.
 */
@Composable
fun AddTodoScreen(
    onBack: () -> Unit,
    viewModel: AddTodoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val currentOnBack by rememberUpdatedState(onBack)

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) currentOnBack()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            viewModel.onEvent(AddTodoEvent.OnDismissError)
        }
    }

    AddTodoScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddTodoScreenContent(
    uiState: AddTodoUiState,
    onEvent: (AddTodoEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
) {
    val spacing = LocalSpacing.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.todo_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.todo_back),
                        )
                    }
                },
            )
        },
        snackbarHost = snackbarHost,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(spacing.medium)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { onEvent(AddTodoEvent.OnTitleChanged(it)) },
                label = { Text(stringResource(id = R.string.todo_title_label)) },
                placeholder = { Text(stringResource(id = R.string.todo_title_placeholder)) },
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it.asString()) } },
                enabled = !uiState.isLoading,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { onEvent(AddTodoEvent.OnDescriptionChanged(it)) },
                label = { Text(stringResource(id = R.string.todo_description_label)) },
                placeholder = { Text(stringResource(id = R.string.todo_description_placeholder)) },
                enabled = !uiState.isLoading,
                minLines = 5,
                maxLines = 10,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(spacing.large))

            AppButton(
                text = stringResource(id = R.string.todo_create),
                onClick = { onEvent(AddTodoEvent.OnSaveClicked) },
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                variant = AppButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddTodoScreenContentPreview() {
    AppTheme {
        AddTodoScreenContent(
            uiState = AddTodoUiState(title = "Write the release notes"),
            onEvent = {},
            onBack = {},
        )
    }
}
