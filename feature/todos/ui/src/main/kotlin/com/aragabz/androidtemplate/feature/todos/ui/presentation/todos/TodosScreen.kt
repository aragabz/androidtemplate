package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.ErrorScreen
import com.aragabz.androidtemplate.core.ui.screens.LoadingScreen
import com.aragabz.androidtemplate.core.ui.screens.SuccessScreen
import com.aragabz.androidtemplate.core.ui.text.asString
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.ui.R

/**
 * Todos tab: collects [TodosViewModel] state and shows non-blocking errors in a snackbar.
 */
@Composable
fun TodosScreen(
    onAddTodo: () -> Unit,
    onTodoClick: (String) -> Unit,
    viewModel: TodosViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // With nothing listed the error is shown full screen instead, until the user retries.
    val snackbarError = uiState.error?.takeIf { uiState.todos.isNotEmpty() }
    LaunchedEffect(snackbarError) {
        snackbarError?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.onEvent(TodosEvent.OnDismissError)
        }
    }

    TodosScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onAddTodo = onAddTodo,
        onTodoClick = onTodoClick,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodosScreenContent(
    uiState: TodosUiState,
    onEvent: (TodosEvent) -> Unit,
    onAddTodo: () -> Unit,
    onTodoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.todo_list_title)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTodo,
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.todo_add))
            }
        },
        snackbarHost = snackbarHost,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val isEmpty = uiState.todos.isEmpty()
            val allCompleted = uiState.todos.isNotEmpty() && uiState.todos.all { it.isCompleted }

            when {
                uiState.isLoading && isEmpty -> {
                    LoadingScreen(message = stringResource(id = R.string.todo_loading))
                }
                uiState.error != null && isEmpty -> {
                    ErrorScreen(
                        message = uiState.error.asString(),
                        onRetry = { onEvent(TodosEvent.OnRefresh) },
                    )
                }
                isEmpty -> {
                    EmptyScreen(
                        message = stringResource(id = R.string.todo_empty_message),
                        subtitle = stringResource(id = R.string.todo_empty_subtitle),
                        actionLabel = stringResource(id = R.string.todo_add),
                        onAction = onAddTodo,
                    )
                }
                allCompleted -> {
                    SuccessScreen(
                        message = stringResource(id = R.string.todo_completed_message),
                        subtitle = stringResource(id = R.string.todo_completed_subtitle),
                        actionLabel = stringResource(id = R.string.todo_add),
                        onAction = onAddTodo,
                    )
                }
                else -> {
                    TodosList(
                        todos = uiState.todos,
                        onTodoClick = onTodoClick,
                        onToggleTodo = { id -> onEvent(TodosEvent.OnToggleTodo(id)) },
                        onDeleteTodo = { id -> onEvent(TodosEvent.OnDeleteTodo(id)) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun TodosList(
    todos: List<Todo>,
    onTodoClick: (String) -> Unit,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        items(todos, key = { it.id }) { todo ->
            TodoItem(
                todo = todo,
                onClick = { onTodoClick(todo.id) },
                onToggle = { onToggleTodo(todo.id) },
                onDelete = { onDeleteTodo(todo.id) },
            )
        }
    }
}

@Composable
private fun TodoItem(
    todo: Todo,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
            )

            Spacer(modifier = Modifier.width(spacing.small))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration =
                        if (todo.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            null
                        },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                todo.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.todo_delete),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TodosScreenContentPreview() {
    AppTheme {
        TodosScreenContent(
            uiState =
                TodosUiState(
                    todos =
                        listOf(
                            Todo(id = "1", title = "Write the release notes", createdAt = 0L),
                            Todo(id = "2", title = "Review the pull request", isCompleted = true, createdAt = 0L),
                        ),
                ),
            onEvent = {},
            onAddTodo = {},
            onTodoClick = {},
        )
    }
}
