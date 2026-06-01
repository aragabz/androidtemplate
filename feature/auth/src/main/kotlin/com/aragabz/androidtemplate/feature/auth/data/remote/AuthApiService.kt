package com.aragabz.androidtemplate.feature.auth.data.remote

import com.aragabz.androidtemplate.core.common.result.AppResult
import com.aragabz.androidtemplate.feature.auth.data.remote.dto.AuthResponse
import com.aragabz.androidtemplate.feature.auth.data.remote.dto.LoginRequest
import com.aragabz.androidtemplate.feature.auth.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AppResult<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AppResult<AuthResponse>
    
    @POST("auth/logout")
    suspend fun logout(): AppResult<Unit>
    
    @GET("auth/me")
    suspend fun getCurrentUser(): AppResult<AuthResponse>
}
