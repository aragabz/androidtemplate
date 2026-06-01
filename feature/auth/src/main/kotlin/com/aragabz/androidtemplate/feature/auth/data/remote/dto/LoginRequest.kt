package com.aragabz.androidtemplate.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for login request.
 * 
 * @property email User email address
 * @property password User password
 */
@Serializable
public data class LoginRequest(
    val email: String,
    val password: String
)
