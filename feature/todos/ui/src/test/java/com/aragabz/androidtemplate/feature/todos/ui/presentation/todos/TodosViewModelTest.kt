package com.aragabz.androidtemplate.feature.todos.ui.presentation.todos

import app.cash.turbine.test
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.usecase.DeleteTodoUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.GetTodosUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.ToggleTodoUseCase
import com.aragabz.androidtemplate.feature.todos.ui.presentation.FakeTodosRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class TodosViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val todo = Todo(id = "1", title = "Write tests", createdAt = 0L)
    private val repository = FakeTodosRepository(initial = listOf(todo))

    private val viewModel =
        mainDispatcherRule.dispatcher.let { dispatcher ->
            TodosViewModel(
                getTodosUseCase = GetTodosUseCase(repository, dispatcher),
                toggleTodoUseCase = ToggleTodoUseCase(repository, dispatcher),
                deleteTodoUseCase = DeleteTodoUseCase(repository, dispatcher),
            )
        }

    @Test
    fun `shows loading, then the stored todos`() =
        runTest {
            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(listOf(todo), loaded.todos)
            }
        }

    @Test
    fun `toggling and deleting update the list through the one subscription`() =
        runTest {
            viewModel.uiState.test {
                skipItems(2) // loading, loaded

                viewModel.onEvent(TodosEvent.OnToggleTodo(todo.id))
                assertTrue(awaitItem().todos.single().isCompleted)

                viewModel.onEvent(TodosEvent.OnToggleTodo(todo.id))
                assertFalse(awaitItem().todos.single().isCompleted)

                viewModel.onEvent(TodosEvent.OnDeleteTodo(todo.id))
                assertTrue(awaitItem().todos.isEmpty())

                assertEquals(1, repository.getTodosCalls)
            }
        }

    @Test
    fun `refresh replaces the subscription instead of adding one`() =
        runTest {
            viewModel.uiState.test {
                skipItems(2)

                viewModel.onEvent(TodosEvent.OnRefresh)
                assertTrue(awaitItem().isLoading)
                assertFalse(awaitItem().isLoading)
                assertEquals(2, repository.getTodosCalls)

                // Only the latest subscription is live: one write produces exactly one new state.
                viewModel.onEvent(TodosEvent.OnToggleTodo(todo.id))
                assertTrue(awaitItem().todos.single().isCompleted)
                expectNoEvents()
            }
        }

    @Test
    fun `failed action shows an error until it is dismissed`() =
        runTest {
            viewModel.uiState.test {
                skipItems(2)
                repository.failure = IOException("disk full")

                viewModel.onEvent(TodosEvent.OnDeleteTodo(todo.id))
                assertNotNull(awaitItem().error)

                viewModel.onEvent(TodosEvent.OnDismissError)
                assertNull(awaitItem().error)
            }
        }
}
