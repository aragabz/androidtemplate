package com.aragabz.androidtemplate.feature.user.domain.usecase

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.user.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for updating user profile.
 */
public class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    public operator fun invoke(name: String, bio: String?): Flow<AppResult<UserProfile>> {
        return repository.updateProfile(name, bio)
    }
}
