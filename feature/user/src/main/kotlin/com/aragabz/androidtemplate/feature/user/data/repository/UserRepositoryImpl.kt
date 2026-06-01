package com.aragabz.androidtemplate.feature.user.data.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.user.data.mapper.UserMapper.toDomain
import com.aragabz.androidtemplate.feature.user.data.remote.UserApiService
import com.aragabz.androidtemplate.feature.user.data.remote.dto.UpdateProfileRequest
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of [UserRepository].
 */
public class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {
    
    override fun getProfile(): Flow<AppResult<UserProfile>> = flow {
        emit(AppResult.Loading)
        
        val result = apiService.getProfile()
        
        when (result) {
            is AppResult.Success -> {
                emit(AppResult.Success(result.data.toDomain()))
            }
            is AppResult.Error -> emit(result)
            is AppResult.Loading -> emit(result)
        }
    }
    
    override fun updateProfile(name: String, bio: String?): Flow<AppResult<UserProfile>> = flow {
        emit(AppResult.Loading)
        
        val request = UpdateProfileRequest(name = name, bio = bio)
        val result = apiService.updateProfile(request)
        
        when (result) {
            is AppResult.Success -> {
                emit(AppResult.Success(result.data.toDomain()))
            }
            is AppResult.Error -> emit(result)
            is AppResult.Loading -> emit(result)
        }
    }
}
