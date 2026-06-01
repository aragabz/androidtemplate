package com.aragabz.androidtemplate.feature.user.domain.usecase

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting user profile.
 */
public class GetProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    public operator fun invoke(): Flow<AppResult<UserProfile>> {
        return repository.getProfile()
    }
}
