package com.aragabz.androidtemplate.feature.profile.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.NoParamFlowUseCase
import com.aragabz.androidtemplate.feature.profile.domain.model.UserProfile
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : NoParamFlowUseCase<AppResult<UserProfile?>>(dispatcher) {
        override fun execute(): Flow<AppResult<UserProfile?>> = repository.getProfile()
    }
