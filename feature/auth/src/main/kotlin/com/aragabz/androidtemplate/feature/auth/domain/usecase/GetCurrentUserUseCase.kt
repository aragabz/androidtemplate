package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.model.User
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting current authenticated user.
 * 
 * @property repository Authentication repository
 */
public class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    
    /**
     * Gets the current authenticated user.
     * 
     * @return Flow emitting current user data
     */
    public operator fun invoke(): Flow<AppResult<User>> {
        return repository.getCurrentUser()
    }
}
