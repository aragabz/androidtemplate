package com.aragabz.androidtemplate.feature.profile.data.repository

import com.aragabz.androidtemplate.core.common.error.withErrorHandling
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileApi
import com.aragabz.androidtemplate.feature.profile.data.mapper.toDomain
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl
    @Inject
    constructor(
        private val profileApi: ProfileApi,
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : ProfileRepository {
        /**
         * Fetches the signed-in user's profile, again only when the signed-in user changes
         * (not on unrelated preference changes such as the theme).
         */
        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getProfile(): Flow<AppResult<UserProfile?>> =
            userPreferencesRepository.userPreferences
                .map { it.userId }
                .distinctUntilChanged()
                .transformLatest { userId ->
                    if (userId.isNullOrBlank()) {
                        emit(AppResult.Success(null))
                    } else {
                        emit(AppResult.Loading)
                        emit(withErrorHandling { profileApi.getProfile(userId).toDomain() })
                    }
                }
    }
