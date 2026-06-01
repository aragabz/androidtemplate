package com.aragabz.androidtemplate.feature.user.domain.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user operations.
 */
public interface UserRepository {
    
    /**
     * Gets the user's profile.
     */
    public fun getProfile(): Flow<AppResult<UserProfile>>
    
    /**
     * Updates the user's profile.
     */
    public fun updateProfile(name: String, bio: String?): Flow<AppResult<UserProfile>>
}
