package com.aragabz.androidtemplate.feature.profile.data.repository

import com.aragabz.androidtemplate.core.common.error.withErrorHandling
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.profile.data.api.ProfileApi
import com.aragabz.androidtemplate.feature.profile.data.mapper.toDomain
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProfileRepositoryImpl
    @Inject
    constructor(
        private val profileApi: ProfileApi,
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : ProfileRepository {
        override fun getProfile(): Flow<AppResult<UserProfile?>> =
            userPreferencesRepository.userPreferences.map { prefs ->
                val userId = prefs.userId
                if (userId.isNullOrBlank()) {
                    // No user logged in
                    AppResult.Success(null)
                } else {
                    // Fetch profile from API
                    withErrorHandling {
                        profileApi.getProfile(userId).toDomain()
                    }
                }
            }
    }
