package com.aragabz.androidtemplate.feature.auth.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.FlowUseCase
import com.aragabz.androidtemplate.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : FlowUseCase<SignInUseCase.Params, AppResult<Unit>>(dispatcher) {
        data class Params(
            val userId: String,
            val token: String,
        )

        override fun execute(parameters: Params): Flow<AppResult<Unit>> =
            repository.signIn(
                userId = parameters.userId,
                token = parameters.token,
            )
    }
