package com.aragabz.androidtemplate

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.model.AppTheme
import com.aragabz.androidtemplate.core.datastore.model.UserPreferences
import com.aragabz.androidtemplate.core.network.session.SessionManager
import com.aragabz.androidtemplate.core.testing.FakeUserPreferencesRepository
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthSession
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import com.aragabz.androidtemplate.feature.auth.domain.usecase.GetAuthSessionUseCase
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import com.aragabz.androidtemplate.feature.auth.ui.presentation.navigation.AuthRoute
import com.aragabz.androidtemplate.navigation.MainRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatcher = mainDispatcherRule.dispatcher
    private val userPreferencesRepository = FakeUserPreferencesRepository()
    private val preferences = userPreferencesRepository.preferences
    private var signedOut = false

    @Test
    fun `is loading until the session is read`() {
        assertEquals(MainUiState.Loading, createViewModel().uiState.value)
    }

    @Test
    fun `signed-out user starts at sign-in with the stored theme`() =
        runTest(testDispatcher) {
            preferences.value = UserPreferences(theme = AppTheme.DARK)
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(
                MainUiState.Ready(startDestination = AuthRoute, theme = AppTheme.DARK),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `signed-in user starts at the shell and theme changes keep the start destination`() =
        runTest(testDispatcher) {
            preferences.value = UserPreferences(userId = "user", authToken = "token")
            val viewModel = createViewModel()
            advanceUntilIdle()

            preferences.value = preferences.value.copy(userId = null, authToken = null, theme = AppTheme.LIGHT)
            advanceUntilIdle()

            assertEquals(
                MainUiState.Ready(startDestination = MainRoute, theme = AppTheme.LIGHT),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `sign-out clears the session and asks for sign-in until it is shown`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.signOut()
            advanceUntilIdle()

            assertTrue(signedOut)
            assertTrue((viewModel.uiState.value as MainUiState.Ready).isSignInRequired)

            viewModel.onSignInShown()
            advanceUntilIdle()

            assertFalse((viewModel.uiState.value as MainUiState.Ready).isSignInRequired)
        }

    @Test
    fun `unauthorized response signs out and asks for sign-in`() =
        runTest(testDispatcher) {
            val sessionManager = SessionManager()
            val viewModel = createViewModel(sessionManager)
            advanceUntilIdle()

            sessionManager.notifyUnauthorized()
            advanceUntilIdle()

            assertTrue(signedOut)
            assertTrue((viewModel.uiState.value as MainUiState.Ready).isSignInRequired)
        }

    @Test
    fun `language changes skip the stored value`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val languages = mutableListOf<String>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.languageChanges.collect { languages += it }
            }
            advanceUntilIdle()

            preferences.value = preferences.value.copy(language = "es")
            advanceUntilIdle()

            assertEquals(listOf("es"), languages)
        }

    private fun createViewModel(sessionManager: SessionManager = SessionManager()): MainViewModel {
        val authRepository =
            object : AuthRepository {
                override fun getSession(): Flow<AuthSession> =
                    preferences.map { AuthSession(userId = it.userId, token = it.authToken) }

                override fun signIn(
                    userId: String,
                    token: String,
                ): Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit))

                override fun signOut(): Flow<AppResult<Unit>> {
                    signedOut = true
                    return flowOf(AppResult.Loading, AppResult.Success(Unit))
                }
            }
        return MainViewModel(
            sessionManager = sessionManager,
            getAuthSessionUseCase = GetAuthSessionUseCase(authRepository, testDispatcher),
            userPreferencesRepository = userPreferencesRepository,
            signOutUseCase = SignOutUseCase(authRepository, testDispatcher),
        )
    }
}
