package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.NoParamFlowUseCase
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignOutUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : NoParamFlowUseCase<AppResult<Unit>>(dispatcher) {
        override fun execute(): Flow<AppResult<Unit>> = repository.signOut()
    }
