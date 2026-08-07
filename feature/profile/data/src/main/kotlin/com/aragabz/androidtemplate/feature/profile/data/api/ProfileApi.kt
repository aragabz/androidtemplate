package com.aragabz.androidtemplate.feature.profile.data.api

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Profile API endpoints.
 */
interface ProfileApi {
    /**
     * Get user profile by user ID.
     */
    @GET("users/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: String,
    ): ProfileDto
}
