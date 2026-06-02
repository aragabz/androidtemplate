package com.aragabz.androidtemplate.feature.auth.data.repository

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.database.dao.AccountDao
import com.aragabz.androidtemplate.core.database.model.AccountEntity
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthToken
import com.aragabz.androidtemplate.feature.auth.domain.model.User
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Room database for offline operation.
 *
 * @property accountDao Dao for local database accounts
 * @property preferencesRepository Repository for storing user preferences
 * @property ioDispatcher IO dispatcher for background operations
 */
public class AuthRepositoryImpl
    @Inject
    constructor(
        private val accountDao: AccountDao,
        private val preferencesRepository: UserPreferencesRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AuthRepository {

        override fun login(
            email: String,
            password: String,
        ): Flow<AppResult<AuthToken>> =
            flow {
                emit(AppResult.Loading)

                val account = accountDao.getAccountByEmail(email)
                if (account == null) {
                    emit(AppResult.Error(Exception("Account not found")))
                    return@flow
                }

                if (account.passwordHash != password) {
                    emit(AppResult.Error(Exception("Invalid password")))
                    return@flow
                }

                // Save session in DataStore
                val token = "offline_token_${account.id}"
                preferencesRepository.saveUserId(account.id)
                preferencesRepository.saveAuthToken(token)

                emit(AppResult.Success(AuthToken(token = token, user = User(id = account.id, name = account.name, email = account.email))))
            }.flowOn(ioDispatcher)

        override fun register(
            name: String,
            email: String,
            password: String,
        ): Flow<AppResult<AuthToken>> =
            flow {
                emit(AppResult.Loading)

                val existing = accountDao.getAccountByEmail(email)
                if (existing != null) {
                    emit(AppResult.Error(Exception("Account already exists with this email")))
                    return@flow
                }

                val id = UUID.randomUUID().toString()
                val newAccount = AccountEntity(
                    id = id,
                    name = name,
                    email = email,
                    passwordHash = password,
                    bio = "Offline account"
                )

                accountDao.insertAccount(newAccount)

                // Save session in DataStore
                val token = "offline_token_$id"
                preferencesRepository.saveUserId(id)
                preferencesRepository.saveAuthToken(token)

                emit(AppResult.Success(AuthToken(token = token, user = User(id = id, name = name, email = email))))
            }.flowOn(ioDispatcher)

        override fun logout(): Flow<AppResult<Unit>> =
            flow {
                emit(AppResult.Loading)
                preferencesRepository.clearSession()
                emit(AppResult.Success(Unit))
            }.flowOn(ioDispatcher)

        override fun getCurrentUser(): Flow<AppResult<User>> =
            flow {
                emit(AppResult.Loading)
                val prefs = preferencesRepository.userPreferences.first()
                val userId = prefs.userId
                if (userId != null) {
                    val account = accountDao.getAccountById(userId)
                    if (account != null) {
                        emit(AppResult.Success(User(id = account.id, name = account.name, email = account.email)))
                    } else {
                        emit(AppResult.Error(Exception("Account not found")))
                    }
                } else {
                    emit(AppResult.Error(Exception("Not authenticated")))
                }
            }.flowOn(ioDispatcher)

        override fun isAuthenticated(): Flow<Boolean> =
            preferencesRepository
                .userPreferences
                .map { !it.authToken.isNullOrBlank() }
                .flowOn(ioDispatcher)
    }
