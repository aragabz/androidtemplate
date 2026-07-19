package com.aragabz.androidtemplate.feature.profile.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.NoParamFlowUseCase
import com.aragabz.androidtemplate.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SignOutProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : NoParamFlowUseCase<AppResult<Unit>>(dispatcher) {
        override fun execute(): Flow<AppResult<Unit>> = repository.signOut()
    }
