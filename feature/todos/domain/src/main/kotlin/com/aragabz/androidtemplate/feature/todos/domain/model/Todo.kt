package com.aragabz.androidtemplate.feature.todos.domain.model

/**
 * Domain model representing a todo item.
 */
data class Todo(
    val id: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null,
)
