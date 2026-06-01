package com.aragabz.androidtemplate.feature.user.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for user profile.
 */
@Serializable
public data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val bio: String? = null
)

/**
 * Data transfer object for updating user profile.
 */
@Serializable
public data class UpdateProfileRequest(
    val name: String,
    val bio: String? = null
)
