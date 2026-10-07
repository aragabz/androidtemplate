package com.aragabz.androidtemplate.feature.auth.data.repository

import com.aragabz.androidtemplate.core.common.error.withErrorHandling
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.auth.data.api.AuthApi
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthSession
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val authApi: AuthApi,
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : AuthRepository {
        override fun getSession(): Flow<AuthSession> =
            userPreferencesRepository.userPreferences.map { prefs ->
                AuthSession(
                    userId = prefs.userId,
                    token = prefs.authToken,
                )
            }

        override fun signIn(
            userId: String,
            token: String,
        ): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                runCatching {
                    userPreferencesRepository.saveUserId(userId)
                    userPreferencesRepository.saveAuthToken(token)
                }.onSuccess {
                    emit(AppResult.Success(Unit))
                }.onFailure {
                    emit(AppResult.Error(it))
                }
            }

        override fun signOut(): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)

                // Attempt to notify server about logout (best-effort)
                withErrorHandling {
                    authApi.signOut()
                }

                // Clear local session regardless of server response
                // This ensures users can always log out even if offline or server unavailable
                runCatching {
                    userPreferencesRepository.clearSession()
                }.onSuccess {
                    emit(AppResult.Success(Unit))
                }.onFailure { localError ->
                    // Only fail if local clear fails (more critical than server notification)
                    emit(AppResult.Error(localError))
                }
            }
    }
