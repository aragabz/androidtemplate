package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for user logout.
 * 
 * @property repository Authentication repository
 */
public class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    
    /**
     * Executes logout operation.
     * 
     * @return Flow emitting logout result
     */
    public operator fun invoke(): Flow<AppResult<Unit>> {
        return repository.logout()
    }
}
