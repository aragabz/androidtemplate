package com.aragabz.androidtemplate.feature.todos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a todo item in the database.
 */
@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: String,
    val updatedAt: String?,
)
