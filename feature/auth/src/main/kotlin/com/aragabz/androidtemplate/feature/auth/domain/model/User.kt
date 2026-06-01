package com.aragabz.androidtemplate.feature.auth.domain.model

/**
 * Domain model representing a user.
 * 
 * @property id Unique user identifier
 * @property name User full name
 * @property email User email address
 * @property avatar Optional avatar URL
 */
public data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null
)
