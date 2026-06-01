package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for checking if user is authenticated.
 * 
 * @property repository Authentication repository
 */
public class IsAuthenticatedUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    
    /**
     * Checks if user is currently authenticated.
     * 
     * @return Flow emitting authentication status
     */
    public operator fun invoke(): Flow<Boolean> {
        return repository.isAuthenticated()
    }
}
