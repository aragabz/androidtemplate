package com.aragabz.androidtemplate.feature.todos.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TodoModelTest {
    @Test
    fun `todo model preserves constructor values`() {
        val createdTime = System.currentTimeMillis()
        val updatedTime = createdTime + 3600_000 // 1 hour later
        
        val todo =
            Todo(
                id = "todo-1",
                title = "Write tests",
                description = "Cover hardening paths",
                isCompleted = true,
                createdAt = createdTime,
                updatedAt = updatedTime,
            )

        assertEquals("todo-1", todo.id)
        assertEquals("Write tests", todo.title)
        assertEquals(true, todo.isCompleted)
        assertEquals(createdTime, todo.createdAt)
        assertEquals(updatedTime, todo.updatedAt)
    }
}
