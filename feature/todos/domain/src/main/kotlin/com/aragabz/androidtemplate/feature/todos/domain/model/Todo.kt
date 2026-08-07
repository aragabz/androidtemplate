package com.aragabz.androidtemplate.feature.todos.domain.model

/**
 * Domain model representing a todo item.
 *
 * Dates are stored as epoch milliseconds (Long) for consistency and ease of comparison.
 * Use kotlinx.datetime.Instant.fromEpochMilliseconds() to convert to Instant if needed.
 */
data class Todo(
    val id: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long? = null,
)
