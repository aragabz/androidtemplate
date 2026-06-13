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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aragabz.androidtemplate.core.designsystem.components.AppButton
import com.aragabz.androidtemplate.core.designsystem.components.AppButtonVariant
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing

/**
 * Add todo screen - create new todo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    navController: NavController,
    viewModel: AddTodoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                AddTodoNavigationEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    // Show error snackbar
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            viewModel.onEvent(AddTodoEvent.OnDismissError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Todo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                onValueChange = { viewModel.onEvent(AddTodoEvent.OnTitleChanged(it)) },
                label = { Text("Title") },
                placeholder = { Text("Enter todo title") },
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } },
                enabled = !uiState.isLoading,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onEvent(AddTodoEvent.OnDescriptionChanged(it)) },
                label = { Text("Description") },
                placeholder = { Text("Enter description (optional)") },
                enabled = !uiState.isLoading,
                minLines = 5,
                maxLines = 10,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(spacing.large))

            AppButton(
                text = "Create Todo",
                onClick = { viewModel.onEvent(AddTodoEvent.OnSaveClicked) },
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                variant = AppButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
