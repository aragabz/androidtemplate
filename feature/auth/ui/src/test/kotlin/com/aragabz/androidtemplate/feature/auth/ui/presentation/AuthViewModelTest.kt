package com.aragabz.androidtemplate.feature.auth.ui.presentation

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.common.ui.UiText
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthSession
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignInUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import com.aragabz.androidtemplate.feature.auth.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is unauthenticated`() = runTest {
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
    fun `onEmailChanged updates email and clears email error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
        advanceUntilIdle()

        assertEquals("test@example.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `onPasswordChanged updates password and clears password error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
        advanceUntilIdle()

        assertEquals("password123", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `onToggleMode switches between sign in and sign up`() = runTest {
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
    fun `onToggleMode clears errors`() = runTest {
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
    fun `submit with blank email shows validation error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
        viewModel.onEvent(AuthEvent.OnSubmit)
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(R.string.auth_error_invalid_email),
            viewModel.uiState.value.emailError
        )
    }

    @Test
    fun `submit with invalid email shows validation error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnEmailChanged("not-an-email"))
        viewModel.onEvent(AuthEvent.OnPasswordChanged("password123"))
        viewModel.onEvent(AuthEvent.OnSubmit)
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(R.string.auth_error_invalid_email),
            viewModel.uiState.value.emailError
        )
    }

    @Test
    fun `submit with short password shows validation error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnEmailChanged("test@example.com"))
        viewModel.onEvent(AuthEvent.OnPasswordChanged("12345"))
        viewModel.onEvent(AuthEvent.OnSubmit)
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(R.string.auth_error_password_too_short),
            viewModel.uiState.value.passwordError
        )
    }

    @Test
    fun `submit with valid credentials triggers sign in`() = runTest {
        val viewModel = createViewModel(
            signInResult = flowOf(
                AppResult.Loading,
                AppResult.Success(Unit)
            )
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
    fun `sign in failure shows error`() = runTest {
        val errorMessage = UiText.DynamicString("Sign in failed")
        val viewModel = createViewModel(
            signInResult = flowOf(
                AppResult.Loading,
                AppResult.Error(com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                    Throwable("Failed")
                ))
            )
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
    fun `sign in shows loading state`() = runTest {
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
    fun `sign out clears user data on success`() = runTest {
        val viewModel = createViewModel(
            authSession = flowOf(AuthSession(isAuthenticated = true, userId = "test@example.com")),
            signOutResult = flowOf(
                AppResult.Loading,
                AppResult.Success(Unit)
            )
        )
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnSignOut)
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.email)
        assertEquals("", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.currentUserEmail)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `sign out failure shows error`() = runTest {
        val viewModel = createViewModel(
            authSession = flowOf(AuthSession(isAuthenticated = true, userId = "test@example.com")),
            signOutResult = flowOf(
                AppResult.Loading,
                AppResult.Error(com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                    Throwable("Failed")
                ))
            )
        )
        advanceUntilIdle()

        viewModel.onEvent(AuthEvent.OnSignOut)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.error != null)
    }

    @Test
    fun `onDismissError clears error`() = runTest {
        val viewModel = createViewModel(
            signInResult = flowOf(
                AppResult.Error(com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                    Throwable("Error")
                ))
            )
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
    fun `auth session updates state`() = runTest {
        val viewModel = createViewModel(
            authSession = flowOf(
                AuthSession(isAuthenticated = false, userId = null),
                AuthSession(isAuthenticated = true, userId = "user@example.com")
            )
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAuthenticated)
        assertEquals("user@example.com", viewModel.uiState.value.currentUserEmail)
    }

    private fun createViewModel(
        authSession: Flow<AuthSession> = flowOf(AuthSession(isAuthenticated = false, userId = null)),
        signInResult: Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit)),
        signOutResult: Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit))
    ): AuthViewModel {
        val getAuthSessionUseCase = object : GetAuthSessionUseCase {
            override fun invoke(): Flow<AuthSession> = authSession
        }

        val signInUseCase = object : SignInUseCase {
            override fun invoke(params: SignInUseCase.Params): Flow<AppResult<Unit>> = signInResult
        }

        val signOutUseCase = object : SignOutUseCase {
            override fun invoke(): Flow<AppResult<Unit>> = signOutResult
        }

        return AuthViewModel(getAuthSessionUseCase, signInUseCase, signOutUseCase)
    }
}
