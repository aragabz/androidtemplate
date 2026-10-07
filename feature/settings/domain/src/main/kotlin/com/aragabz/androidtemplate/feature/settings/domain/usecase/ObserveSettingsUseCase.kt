package com.aragabz.androidtemplate.feature.settings.domain.usecase

import com.aragabz.androidtemplate.core.common.di.IoDispatcher
import com.aragabz.androidtemplate.core.domain.usecase.NoParamFlowUseCase
import com.aragabz.androidtemplate.feature.settings.domain.model.SettingsPreferences
import com.aragabz.androidtemplate.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase
    @Inject
    constructor(
        private val repository: SettingsRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) : NoParamFlowUseCase<SettingsPreferences>(dispatcher) {
        override fun execute(): Flow<SettingsPreferences> = repository.observeSettings()
    }
