package com.aragabz.androidtemplate.feature.auth.domain.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthToken
import com.aragabz.androidtemplate.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 */
public interface AuthRepository {
    
    /**
     * Authenticates user with email and password.
     * 
     * @param email User email address
     * @param password User password
     * @return Flow emitting authentication result with token
     */
    public fun login(email: String, password: String): Flow<AppResult<AuthToken>>
    
    /**
     * Registers a new user.
     * 
     * @param name User full name
     * @param email User email address
     * @param password User password
     * @return Flow emitting registration result with token
     */
    public fun register(name: String, email: String, password: String): Flow<AppResult<AuthToken>>
    
    /**
     * Logs out the current user.
     * 
     * @return Flow emitting logout result
     */
    public fun logout(): Flow<AppResult<Unit>>
    
    /**
     * Gets the current authenticated user.
     * 
     * @return Flow emitting current user data
     */
    public fun getCurrentUser(): Flow<AppResult<User>>
    
    /**
     * Checks if user is authenticated.
     * 
     * @return Flow emitting authentication status
     */
    public fun isAuthenticated(): Flow<Boolean>
}
