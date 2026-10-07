package com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo

import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.core.ui.text.UiText
import com.aragabz.androidtemplate.feature.todos.domain.usecase.AddTodoUseCase
import com.aragabz.androidtemplate.feature.todos.ui.R
import com.aragabz.androidtemplate.feature.todos.ui.presentation.FakeTodosRepository
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AddTodoViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeTodosRepository()
    private val viewModel =
        AddTodoViewModel(addTodoUseCase = AddTodoUseCase(repository, mainDispatcherRule.dispatcher))

    @Test
    fun `save with blank title sets validation error`() =
        runTest {
            viewModel.onEvent(AddTodoEvent.OnSaveClicked)

            assertEquals(UiText.StringResource(R.string.todo_title_required), viewModel.uiState.value.titleError)
            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(repository.todos.value.isEmpty())
        }

    @Test
    fun `save with valid title stores the trimmed todo and marks the state saved`() =
        runTest {
            viewModel.onEvent(AddTodoEvent.OnTitleChanged(" Ship hardening "))
            viewModel.onEvent(AddTodoEvent.OnSaveClicked)
            advanceUntilIdle()

            assertEquals(
                "Ship hardening",
                repository.todos.value
                    .single()
                    .title,
            )
            assertTrue(viewModel.uiState.value.isSaved)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `save failure shows an error until it is dismissed`() =
        runTest {
            repository.failure = IOException("disk full")

            viewModel.onEvent(AddTodoEvent.OnTitleChanged("Ship hardening"))
            viewModel.onEvent(AddTodoEvent.OnSaveClicked)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isSaved)
            assertNotNull(viewModel.uiState.value.error)

            viewModel.onEvent(AddTodoEvent.OnDismissError)

            assertNull(viewModel.uiState.value.error)
        }
}
