package com.aragabz.androidtemplate.feature.auth.data.api

import retrofit2.http.POST

/**
 * Authentication API endpoints.
 */
interface AuthApi {
    /**
     * Signs out the current user from the server.
     * This endpoint notifies the server to invalidate the user's session/token.
     */
    @POST("auth/logout")
    suspend fun signOut()
}
