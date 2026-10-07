package com.aragabz.androidtemplate.feature.settings.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.core.domain.usecase.FlowUseCase
import com.aragabz.androidtemplate.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateLanguageUseCase
    @Inject
    constructor(
        private val repository: SettingsRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : FlowUseCase<String, AppResult<Unit>>(dispatcher) {
        override fun execute(parameters: String): Flow<AppResult<Unit>> = repository.updateLanguage(parameters)
    }
