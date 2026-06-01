package com.aragabz.androidtemplate.feature.auth.data.repository

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.auth.data.mapper.AuthMapper.toDomain
import com.aragabz.androidtemplate.feature.auth.data.remote.AuthApiService
import com.aragabz.androidtemplate.feature.auth.data.remote.dto.LoginRequest
import com.aragabz.androidtemplate.feature.auth.data.remote.dto.RegisterRequest
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthToken
import com.aragabz.androidtemplate.feature.auth.domain.model.User
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Retrofit API service.
 *
 * @property apiService Retrofit service for authentication endpoints
 * @property preferencesRepository Repository for storing user preferences
 * @property ioDispatcher IO dispatcher for background operations
 */
public class AuthRepositoryImpl
    @Inject
    constructor(
        private val apiService: AuthApiService,
        private val preferencesRepository: UserPreferencesRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AuthRepository {
        override fun login(
            email: String,
            password: String,
        ): Flow<AppResult<AuthToken>> =
            flow {
                emit(AppResult.Loading)

                val request = LoginRequest(email = email, password = password)
                val result = apiService.login(request)

                when (result) {
                    is AppResult.Success -> {
                        val authToken = result.data.toDomain()
                        // Save token to DataStore
                        preferencesRepository.saveAuthToken(authToken.token)
                        emit(AppResult.Success(authToken))
                    }

                    is AppResult.Error -> {
                        emit(result)
                    }

                    is AppResult.Loading -> {
                        emit(result)
                    }
                }
            }.flowOn(ioDispatcher)

        override fun register(
            name: String,
            email: String,
            password: String,
        ): Flow<AppResult<AuthToken>> =
            flow {
                emit(AppResult.Loading)

                val request = RegisterRequest(name = name, email = email, password = password)
                val result = apiService.register(request)

                when (result) {
                    is AppResult.Success -> {
                        val authToken = result.data.toDomain()
                        // Save token to DataStore
                        preferencesRepository.saveAuthToken(authToken.token)
                        emit(AppResult.Success(authToken))
                    }

                    is AppResult.Error -> {
                        emit(result)
                    }

                    is AppResult.Loading -> {
                        emit(result)
                    }
                }
            }.flowOn(ioDispatcher)

        override fun logout(): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)

                val result = apiService.logout()

                when (result) {
                    is AppResult.Success -> {
                        // Clear session from DataStore
                        preferencesRepository.clearSession()
                        emit(AppResult.Success(Unit))
                    }

                    is AppResult.Error -> {
                        // Even if API fails, clear local session
                        preferencesRepository.clearSession()
                        emit(AppResult.Success(Unit))
                    }

                    is AppResult.Loading -> {
                        emit(result)
                    }
                }
            }.flowOn(ioDispatcher)

        override fun getCurrentUser(): Flow<AppResult<User>> =
            flow {
                emit(AppResult.Loading)

                val result = apiService.getCurrentUser()

                when (result) {
                    is AppResult.Success -> {
                        val user = result.data.user.toDomain()
                        emit(AppResult.Success(user))
                    }

                    is AppResult.Error -> {
                        emit(result)
                    }

                    is AppResult.Loading -> {
                        emit(result)
                    }
                }
            }.flowOn(ioDispatcher)

        override fun isAuthenticated(): Flow<Boolean> =
            preferencesRepository
                .userPreferences
                .map { it.authToken?.isNotBlank() ?: false }
                .flowOn(ioDispatcher)
    }
