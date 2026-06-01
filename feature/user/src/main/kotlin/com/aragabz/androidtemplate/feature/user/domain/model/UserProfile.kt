package com.aragabz.androidtemplate.feature.user.domain.model

/**
 * Domain model representing a user profile.
 */
public data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val bio: String? = null
)
