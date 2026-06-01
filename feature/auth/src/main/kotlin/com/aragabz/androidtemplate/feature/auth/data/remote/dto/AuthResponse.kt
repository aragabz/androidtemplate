package com.aragabz.androidtemplate.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for authentication response.
 * 
 * @property token JWT authentication token
 * @property user User data
 */
@Serializable
public data class AuthResponse(
    val token: String,
    val user: UserDto
)

/**
 * Data transfer object for user information.
 * 
 * @property id Unique user identifier
 * @property name User full name
 * @property email User email address
 * @property avatar Optional avatar URL
 */
@Serializable
public data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null
)
