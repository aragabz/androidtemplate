package com.aragabz.androidtemplate.feature.user.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.database.dao.AccountDao
import com.aragabz.androidtemplate.core.datastore.UserPreferencesRepository
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of [UserRepository] using Room database for offline profile operations.
 */
public class UserRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val preferencesRepository: UserPreferencesRepository
) : UserRepository {
    
    override fun getProfile(): Flow<AppResult<UserProfile>> = flow {
        emit(AppResult.Loading)
        
        val prefs = preferencesRepository.userPreferences.first()
        val userId = prefs.userId
        if (userId != null) {
            val account = accountDao.getAccountById(userId)
            if (account != null) {
                emit(AppResult.Success(
                    UserProfile(
                        id = account.id,
                        name = account.name,
                        email = account.email,
                        bio = account.bio
                    )
                ))
            } else {
                emit(AppResult.Error(Exception("Profile account not found in database")))
            }
        } else {
            emit(AppResult.Error(Exception("No active user session")))
        }
    }
    
    override fun updateProfile(name: String, bio: String?): Flow<AppResult<UserProfile>> = flow {
        emit(AppResult.Loading)
        
        val prefs = preferencesRepository.userPreferences.first()
        val userId = prefs.userId
        if (userId != null) {
            val account = accountDao.getAccountById(userId)
            if (account != null) {
                val updated = account.copy(name = name, bio = bio)
                accountDao.insertAccount(updated)
                
                emit(AppResult.Success(
                    UserProfile(
                        id = updated.id,
                        name = updated.name,
                        email = updated.email,
                        bio = updated.bio
                    )
                ))
            } else {
                emit(AppResult.Error(Exception("Profile account not found in database")))
            }
        } else {
            emit(AppResult.Error(Exception("No active user session")))
        }
    }
}
