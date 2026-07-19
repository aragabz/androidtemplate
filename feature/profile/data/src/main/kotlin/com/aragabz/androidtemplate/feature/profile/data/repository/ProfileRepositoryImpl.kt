package com.aragabz.androidtemplate.feature.profile.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class ProfileRepositoryImpl
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : ProfileRepository {
        override fun getProfile(): Flow<AppResult<UserProfile?>> =
            userPreferencesRepository.userPreferences.map { prefs ->
                val userId = prefs.userId
                if (userId.isNullOrBlank()) {
                    AppResult.Success(null)
                } else {
                    AppResult.Success(
                        UserProfile(
                            userId = userId,
                            displayName = userId.replaceFirstChar { it.uppercase() },
                            email = "$userId@example.com",
                        ),
                    )
                }
            }

        override fun signOut(): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                runCatching {
                    userPreferencesRepository.clearSession()
                }.onSuccess {
                    emit(AppResult.Success(Unit))
                }.onFailure {
                    emit(AppResult.Error(it))
                }
            }
    }
