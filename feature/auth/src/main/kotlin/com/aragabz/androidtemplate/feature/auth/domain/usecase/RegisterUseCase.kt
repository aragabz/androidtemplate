package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthToken
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for user registration.
 * 
 * @property repository Authentication repository
 */
public class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    
    /**
     * Executes registration operation.
     * 
     * @param name User full name
     * @param email User email address
     * @param password User password
     * @return Flow emitting registration result
     */
    public operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Flow<AppResult<AuthToken>> {
        return repository.register(name, email, password)
    }
}
