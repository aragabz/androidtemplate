package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthToken
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for user login.
 * 
 * @property repository Authentication repository
 */
public class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    
    /**
     * Executes login operation.
     * 
     * @param email User email address
     * @param password User password
     * @return Flow emitting login result
     */
    public operator fun invoke(email: String, password: String): Flow<AppResult<AuthToken>> {
        return repository.login(email, password)
    }
}
