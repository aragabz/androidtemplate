package com.aragabz.androidtemplate.feature.todos.ui.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.common.util.DateTimeUtils
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.ui.R

/**
 * Todo details screen: collects [TodoDetailsViewModel] state and leaves once the todo is deleted.
 */
@Composable
fun TodoDetailsScreen(
    onBack: () -> Unit,
    viewModel: TodoDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val currentOnBack by rememberUpdatedState(onBack)

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) currentOnBack()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            viewModel.onEvent(TodoDetailsEvent.OnDismissError)
        }
    }

    TodoDetailsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoDetailsScreenContent(
    uiState: TodoDetailsUiState,
    onEvent: (TodoDetailsEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.todo_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.todo_back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = uiState.todo != null,
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.todo_delete),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )
        },
        snackbarHost = snackbarHost,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    uiState.todo?.let { todo ->
                        TodoDetailsBody(
                            todo = todo,
                            onToggleComplete = { onEvent(TodoDetailsEvent.OnToggleTodo) },
                            modifier = Modifier.fillMaxSize(),
                        )
                    } ?: Text(
                        text = stringResource(id = R.string.todo_not_found),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.todo_delete_title)) },
            text = { Text(stringResource(id = R.string.todo_delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onEvent(TodoDetailsEvent.OnDeleteTodo)
                    },
                ) {
                    Text(stringResource(id = R.string.todo_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(id = R.string.todo_cancel))
                }
            },
        )
    }
}

@Composable
private fun TodoDetailsBody(
    todo: Todo,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
    ) {
        // Completion status card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                )
                Text(
                    text = if (todo.isCompleted) {
                        stringResource(
                            id = R.string.todo_status_completed,
                        )
                    } else {
                        stringResource(id = R.string.todo_status_not_completed)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = spacing.small),
                )
                if (todo.isCompleted) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.large))

        // Title
        Text(
            text = stringResource(id = R.string.todo_title_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        Text(
            text = todo.title,
            style = MaterialTheme.typography.headlineMedium,
            textDecoration =
                if (todo.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                },
        )

        val description = todo.description
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(spacing.large))
            Text(
                text = stringResource(id = R.string.todo_description_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.height(spacing.large))

        // Metadata
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                MetadataRow(
                    label = stringResource(id = R.string.todo_created),
                    value = DateTimeUtils.formatDateTime(todo.createdAt),
                )
                todo.updatedAt?.let { updatedAt ->
                    MetadataRow(
                        label = stringResource(id = R.string.todo_last_updated),
                        value = DateTimeUtils.formatDateTime(updatedAt),
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun TodoDetailsScreenContentPreview() {
    AppTheme {
        TodoDetailsScreenContent(
            uiState =
                TodoDetailsUiState(
                    todo =
                        Todo(
                            id = "1",
                            title = "Write the release notes",
                            description = "Summarize the changes since the last release.",
                            createdAt = 0L,
                        ),
                ),
            onEvent = {},
            onBack = {},
        )
    }
}
