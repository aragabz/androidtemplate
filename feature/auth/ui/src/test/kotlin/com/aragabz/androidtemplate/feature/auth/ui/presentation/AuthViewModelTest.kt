package com.aragabz.androidtemplate.feature.auth.ui.presentation

import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthSession
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignInUseCase
import com.aragabz.androidtemplate.feature.auth.ui.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state is unauthenticated`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isAuthenticated)
            assertFalse(state.isLoading)
            assertEquals("", state.email)
            assertEquals("", state.password)
            assertNull(state.error)
        }

    @Test
    fun `onEmailChanged updates email and clears email error`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
            advanceUntilIdle()

            assertEquals("test@example.com", viewModel.uiState.value.email)
            assertNull(viewModel.uiState.value.emailError)
        }

    @Test
    fun `onPasswordChanged updates password and clears password error`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            advanceUntilIdle()

            assertEquals("password123", viewModel.uiState.value.password)
            assertNull(viewModel.uiState.value.passwordError)
        }

    @Test
    fun `onToggleMode switches between sign in and sign up`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isSignUpMode)

            viewModel.onEvent(AuthEvent.OnToggleMode)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isSignUpMode)

            viewModel.onEvent(AuthEvent.OnToggleMode)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isSignUpMode)
        }

    @Test
    fun `onToggleMode clears errors`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Set some errors first by submitting invalid data
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            // Toggle mode should clear errors
            viewModel.onEvent(AuthEvent.OnToggleMode)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.emailError)
            assertNull(viewModel.uiState.value.passwordError)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `submit with blank email shows validation error`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            assertEquals(
                UiText.StringResource(R.string.auth_error_invalid_email),
                viewModel.uiState.value.emailError,
            )
        }

    @Test
    fun `submit with invalid email shows validation error`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnEmailChanged("not-an-email"))
            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            assertEquals(
                UiText.StringResource(R.string.auth_error_invalid_email),
                viewModel.uiState.value.emailError,
            )
        }

    @Test
    fun `submit with short password shows validation error`() =
        runTest {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
            viewModel.onEvent(AuthEvent.OnPasswordChanged("12345"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            assertEquals(
                UiText.StringResource(R.string.auth_error_password_too_short),
                viewModel.uiState.value.passwordError,
            )
        }

    @Test
    fun `submit with valid credentials triggers sign in`() =
        runTest {
            val viewModel = createViewModel(
                signInResult = flowOf(
                    AppResult.Loading,
                    AppResult.Success(Unit),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            // Password should be cleared on success
            assertEquals("", viewModel.uiState.value.password)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `sign in failure shows error`() =
        runTest {
            val viewModel = createViewModel(
                signInResult = flowOf(
                    AppResult.Loading,
                    AppResult.Error(
                        AppError.UnknownError(
                            Throwable("Failed"),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            // Error should be set (specific error text depends on error mapper)
            assertTrue(viewModel.uiState.value.error != null)
        }

    @Test
    fun `sign in shows loading state`() =
        runTest {
            // Create a flow that stays in Loading state
            val loadingFlow = flowOf(AppResult.Loading)
            val viewModel = createViewModel(signInResult = loadingFlow)
            advanceUntilIdle()

            viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `onDismissError clears error`() =
        runTest {
            val viewModel = createViewModel(
                signInResult = flowOf(
                    AppResult.Error(
                        AppError.UnknownError(
                            Throwable("Error"),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            // Trigger an error
            viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
            viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(AuthEvent.OnSubmit)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.error != null)

            // Dismiss error
            viewModel.onEvent(AuthEvent.OnDismissError)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `auth session updates state`() =
        runTest {
            val viewModel = createViewModel(
                authSession = flowOf(
                    AuthSession(),
                    AuthSession(userId = "user@example.com", token = "token"),
                ),
            )
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isAuthenticated)
        }

    private fun createViewModel(
        authSession: Flow<AuthSession> = flowOf(AuthSession()),
        signInResult: Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit)),
    ): AuthViewModel {
        val repository = FakeAuthRepository(authSession, signInResult)
        return AuthViewModel(
            getAuthSessionUseCase = GetAuthSessionUseCase(repository, mainDispatcherRule.dispatcher),
            signInUseCase = SignInUseCase(repository, mainDispatcherRule.dispatcher),
        )
    }

    private class FakeAuthRepository(
        private val session: Flow<AuthSession>,
        private val signInResult: Flow<AppResult<Unit>>,
    ) : AuthRepository {
        override fun getSession(): Flow<AuthSession> = session

        override fun signIn(
            userId: String,
            token: String,
        ): Flow<AppResult<Unit>> = signInResult

        override fun signOut(): Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit))
    }
}
