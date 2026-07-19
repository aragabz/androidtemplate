package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aragabz.androidtemplate.core.navigation.Route
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.ErrorScreen
import com.aragabz.androidtemplate.core.ui.screens.LoadingScreen
import com.aragabz.androidtemplate.core.ui.screens.SuccessScreen
import com.aragabz.androidtemplate.feature.todos.ui.R
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo

/**
 * Todos screen showing list of todos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodosScreen(
    navController: NavController,
    viewModel: TodosViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = androidx.compose.ui.platform.LocalContext.current

    TodosScreenContent(
        uiState = uiState,
        errorMessage = uiState.error?.asString(context),
        onAddTodo = { navController.navigate(Route.AddTodo) },
        onTodoClick = { id -> navController.navigate(Route.TodoDetails(id)) },
        onToggleTodo = { id -> viewModel.onEvent(TodosEvent.OnToggleTodo(id)) },
        onDeleteTodo = { id -> viewModel.onEvent(TodosEvent.OnDeleteTodo(id)) },
        onRefresh = { viewModel.onEvent(TodosEvent.OnRefresh) },
        onDismissError = { viewModel.onEvent(TodosEvent.OnDismissError) },
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodosScreenContent(
    uiState: TodosUiState,
    errorMessage: String?,
    onAddTodo: () -> Unit,
    onTodoClick: (String) -> Unit,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit,
    onRefresh: () -> Unit,
    onDismissError: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val isEmpty = uiState.todos.isEmpty()
            val allCompleted = uiState.todos.isNotEmpty() && uiState.todos.all { it.isCompleted }

            when {
                uiState.isLoading && isEmpty -> {
                    LoadingScreen(message = stringResource(id = R.string.todo_loading))
                }
                errorMessage != null && isEmpty -> {
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = onRefresh,
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
                        onToggleTodo = onToggleTodo,
                        onDeleteTodo = onDeleteTodo,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            if (errorMessage != null && !isEmpty) {
                LaunchedEffect(errorMessage) {
                    snackbarHostState.showSnackbar(errorMessage)
                    onDismissError()
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
    LazyColumn(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

            Spacer(modifier = Modifier.width(8.dp))

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
