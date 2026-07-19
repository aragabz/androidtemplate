package com.aragabz.androidtemplate.feature.auth.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthSession
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
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
                runCatching {
                    userPreferencesRepository.clearSession()
                }.onSuccess {
                    emit(AppResult.Success(Unit))
                }.onFailure {
                    emit(AppResult.Error(it))
                }
            }
    }
