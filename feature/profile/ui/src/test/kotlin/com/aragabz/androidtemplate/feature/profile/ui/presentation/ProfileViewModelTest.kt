package com.aragabz.androidtemplate.feature.profile.ui.presentation

import app.cash.turbine.test
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import com.aragabz.androidtemplate.core.testing.awaitItemMatching
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import com.aragabz.androidtemplate.feature.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val profile = UserProfile(userId = "user-123", displayName = "John Doe", email = "john@example.com")

    /** Answers each subscription with the next queued result stream, and counts subscriptions. */
    private class FakeProfileRepository(
        private val results: ArrayDeque<Flow<AppResult<UserProfile?>>>,
    ) : ProfileRepository {
        var getProfileCalls = 0
            private set

        override fun getProfile(): Flow<AppResult<UserProfile?>> {
            getProfileCalls++
            return results.removeFirst()
        }
    }

    private fun createViewModel(
        vararg results: Flow<AppResult<UserProfile?>>,
    ): Pair<ProfileViewModel, FakeProfileRepository> {
        val repository = FakeProfileRepository(ArrayDeque(results.toList()))
        return ProfileViewModel(GetProfileUseCase(repository, mainDispatcherRule.dispatcher)) to repository
    }

    @Test
    fun `shows loading, then the profile`() =
        runTest {
            val (viewModel, _) = createViewModel(flowOf(AppResult.Loading, AppResult.Success(profile)))

            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(profile, loaded.profile)
                assertNull(loaded.error)
            }
        }

    @Test
    fun `load failure shows an error until it is dismissed`() =
        runTest {
            val (viewModel, _) = createViewModel(flowOf(AppResult.Loading, AppResult.Error(IOException("offline"))))

            viewModel.uiState.test {
                val failed = awaitItemMatching { !it.isLoading }
                assertFalse(failed.isLoading)
                assertNotNull(failed.error)

                viewModel.onEvent(ProfileEvent.OnDismissError)
                assertNull(awaitItem().error)
            }
        }

    @Test
    fun `refresh replaces the subscription instead of adding one`() =
        runTest {
            val updated = profile.copy(displayName = "Jane Doe")
            val (viewModel, repository) =
                createViewModel(
                    flowOf(AppResult.Success(profile)),
                    flowOf(AppResult.Loading, AppResult.Success(updated)),
                )

            viewModel.uiState.test {
                skipItems(1)
                assertEquals(profile, awaitItem().profile)

                viewModel.onEvent(ProfileEvent.OnRefresh)
                assertTrue(awaitItem().isLoading)
                assertEquals(updated, awaitItem().profile)
                assertEquals(2, repository.getProfileCalls)
            }
        }
}
