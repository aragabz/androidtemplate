package com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo

import app.cash.turbine.test
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.todos.domain.model.Todo
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import com.aragabz.androidtemplate.feature.todos.domain.usecase.AddTodoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTodoViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `save with blank title sets validation error`() =
        runTest {
            val viewModel = AddTodoViewModel(addTodoUseCase = addTodoUseCaseWith(flowOf(AppResult.Loading)))

            viewModel.onEvent(AddTodoEvent.OnSaveClicked)

            assertEquals("Title is required", viewModel.uiState.value.titleError)
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `save with valid title emits navigation event on success`() =
        runTest {
            val todo =
                Todo(
                    id = "id-1",
                    title = "Ship hardening",
                    description = null,
                    isCompleted = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = null,
                )
            val viewModel =
                AddTodoViewModel(
                    addTodoUseCase =
                        addTodoUseCaseWith(
                            flowOf(
                                AppResult.Loading,
                                AppResult.Success(todo),
                            ),
                        ),
                )

            viewModel.onEvent(AddTodoEvent.OnTitleChanged(" Ship hardening "))
            viewModel.onEvent(AddTodoEvent.OnSaveClicked)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.error == null)

            viewModel.navigationEvents.test {
                assertEquals(AddTodoNavigationEvent.NavigateBack, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun addTodoUseCaseWith(results: Flow<AppResult<Todo>>): AddTodoUseCase {
        val repository =
            object : TodosRepository {
                override fun getTodos(): Flow<AppResult<List<Todo>>> =
                    throw UnsupportedOperationException("Not used in this test")

                override fun getTodoById(id: String): Flow<AppResult<Todo>> =
                    throw UnsupportedOperationException("Not used in this test")

                override fun addTodo(title: String, description: String?): Flow<AppResult<Todo>> = results

                override fun toggleTodo(id: String): Flow<AppResult<Todo>> =
                    throw UnsupportedOperationException("Not used in this test")

                override fun deleteTodo(id: String): Flow<AppResult<Unit>> =
                    throw UnsupportedOperationException("Not used in this test")
            }

        return AddTodoUseCase(repository = repository, dispatcher = mainDispatcherRule.dispatcher)
    }
}
