package com.aragabz.androidtemplate.feature.profile.ui.presentation

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.usecase.SignOutUseCase
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.profile.domain.usecase.GetProfileUseCase
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
class ProfileViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val testProfile = UserProfile(
        userId = "user-123",
        displayName = "John Doe",
        email = "john@example.com",
        avatarUrl = "https://example.com/avatar.jpg"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(AppResult.Loading)
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.profile)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `init loads profile successfully`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(
                AppResult.Loading,
                AppResult.Success(testProfile)
            )
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(testProfile, viewModel.uiState.value.profile)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `init shows error on profile load failure`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(
                AppResult.Loading,
                AppResult.Error(
                    com.aragabz.androidtemplate.core.common.result.AppError.NetworkError(
                        Exception("Network error")
                    )
                )
            )
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.profile)
        assertTrue(viewModel.uiState.value.error != null)
    }

    @Test
    fun `onRefresh loads profile again`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(
                AppResult.Loading,
                AppResult.Success(testProfile)
            )
        )
        advanceUntilIdle()

        assertEquals(testProfile, viewModel.uiState.value.profile)

        viewModel.onEvent(ProfileEvent.OnRefresh)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onSignOut clears profile on success`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(AppResult.Success(testProfile)),
            signOutResult = flowOf(
                AppResult.Loading,
                AppResult.Success(Unit)
            )
        )
        advanceUntilIdle()

        assertEquals(testProfile, viewModel.uiState.value.profile)

        viewModel.onEvent(ProfileEvent.OnSignOut)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.profile)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onSignOut shows error on failure`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(AppResult.Success(testProfile)),
            signOutResult = flowOf(
                AppResult.Loading,
                AppResult.Error(
                    com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                        Throwable("Sign out failed")
                    )
                )
            )
        )
        advanceUntilIdle()

        viewModel.onEvent(ProfileEvent.OnSignOut)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.error != null)
        assertEquals(testProfile, viewModel.uiState.value.profile)
    }

    @Test
    fun `onDismissError clears error`() = runTest {
        val viewModel = createViewModel(
            profileResult = flowOf(
                AppResult.Error(
                    com.aragabz.androidtemplate.core.common.result.AppError.UnknownError(
                        Throwable("Error")
                    )
                )
            )
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)

        viewModel.onEvent(ProfileEvent.OnDismissError)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    private fun createViewModel(
        profileResult: Flow<AppResult<UserProfile>> = flowOf(AppResult.Success(testProfile)),
        signOutResult: Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit))
    ): ProfileViewModel {
        val getProfileUseCase = object : GetProfileUseCase {
            override fun invoke(): Flow<AppResult<UserProfile>> = profileResult
        }

        val signOutUseCase = object : SignOutUseCase {
            override fun invoke(): Flow<AppResult<Unit>> = signOutResult
        }

        return ProfileViewModel(getProfileUseCase, signOutUseCase)
    }
}
