package com.aragabz.androidtemplate.feature.todos.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TodoModelTest {
    @Test
    fun `todo model preserves constructor values`() {
        val todo =
            Todo(
                id = "todo-1",
                title = "Write tests",
                description = "Cover hardening paths",
                isCompleted = true,
                createdAt = "2026-07-18T00:00:00Z",
                updatedAt = "2026-07-18T01:00:00Z",
            )

        assertEquals("todo-1", todo.id)
        assertEquals("Write tests", todo.title)
        assertEquals(true, todo.isCompleted)
    }
}
