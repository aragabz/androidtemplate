package com.aragabz.androidtemplate.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for registration request.
 * 
 * @property name User full name
 * @property email User email address
 * @property password User password
 */
@Serializable
public data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
