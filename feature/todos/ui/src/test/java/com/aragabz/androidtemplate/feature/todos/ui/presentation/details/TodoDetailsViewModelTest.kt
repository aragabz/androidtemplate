package com.aragabz.androidtemplate.feature.todos.ui.presentation.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.usecase.DeleteTodoUseCase
import com.aragabz.androidtemplate.feature.todos.domain.usecase.GetTodoByIdUseCase
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Reading the type-safe route from SavedStateHandle goes through Android argument types, so this runs on Robolectric.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TodoDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val todo = Todo(id = "1", title = "Write tests", createdAt = 0L)
    private val repository = FakeTodosRepository(initial = listOf(todo))

    private fun createViewModel(id: String = todo.id): TodoDetailsViewModel {
        val dispatcher = mainDispatcherRule.dispatcher
        return TodoDetailsViewModel(
            getTodoByIdUseCase = GetTodoByIdUseCase(repository, dispatcher),
            toggleTodoUseCase = ToggleTodoUseCase(repository, dispatcher),
            deleteTodoUseCase = DeleteTodoUseCase(repository, dispatcher),
            savedStateHandle = SavedStateHandle(mapOf("id" to id)),
        )
    }

    @Test
    fun `loads the todo from the route id`() =
        runTest {
            createViewModel().uiState.test {
                assertTrue(awaitItem().isLoading)
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(todo, loaded.todo)
            }
        }

    @Test
    fun `missing todo shows an error`() =
        runTest {
            createViewModel(id = "missing").uiState.test {
                skipItems(1)
                val state = awaitItem()
                assertNull(state.todo)
                assertNotNull(state.error)
            }
        }

    @Test
    fun `toggle shows the updated todo`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.uiState.test {
                skipItems(2)

                viewModel.onEvent(TodoDetailsEvent.OnToggleTodo)

                assertTrue(awaitItem().todo!!.isCompleted)
            }
        }

    @Test
    fun `delete removes the todo and marks the state deleted`() =
        runTest {
            val viewModel = createViewModel()
            viewModel.uiState.test {
                skipItems(2)

                viewModel.onEvent(TodoDetailsEvent.OnDeleteTodo)

                assertTrue(awaitItem().isDeleted)
                assertTrue(repository.todos.value.isEmpty())
            }
        }
}
