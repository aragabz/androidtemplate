package com.aragabz.androidtemplate.feature.auth.domain.repository

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getSession(): Flow<AuthSession>

    fun signIn(
        userId: String,
        token: String,
    ): Flow<AppResult<Unit>>

    fun signOut(): Flow<AppResult<Unit>>
}
