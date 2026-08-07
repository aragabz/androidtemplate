package com.aragabz.androidtemplate.feature.profile.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Profile data transfer object from API.
 */
@Serializable
data class ProfileDto(
    @SerialName("id")
    val userId: String,
    @SerialName("name")
    val displayName: String,
    @SerialName("email")
    val email: String,
)
