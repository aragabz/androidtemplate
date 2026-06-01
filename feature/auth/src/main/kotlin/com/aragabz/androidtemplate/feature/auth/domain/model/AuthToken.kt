package com.aragabz.androidtemplate.feature.auth.domain.model

/**
 * Domain model representing an authentication token.
 * 
 * @property token JWT token string
 * @property user Associated user data
 */
public data class AuthToken(
    val token: String,
    val user: User
)
