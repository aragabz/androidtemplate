package com.aragabz.androidtemplate.feature.user.data.remote

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.user.data.remote.dto.UpdateProfileRequest
import com.aragabz.androidtemplate.feature.user.data.remote.dto.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * API service for user operations.
 */
public interface UserApiService {
    
    @GET("users/profile")
    suspend fun getProfile(): AppResult<UserProfileDto>
    
    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): AppResult<UserProfileDto>
}
