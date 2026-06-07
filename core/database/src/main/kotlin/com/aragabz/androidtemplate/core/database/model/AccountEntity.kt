package com.aragabz.androidtemplate.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an offline user account.
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
)
