package com.aragabz.androidtemplate.feature.todos.data.local.entity

import com.aragabz.androidtemplate.feature.todos.data.repository.toExternalModel
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoEntityMappingTest {
    @Test
    fun `toExternalModel maps all fields`() {
        val entity =
            TodoEntity(
                id = "todo-1",
                userId = "user-1",
                title = "Buy milk",
                description = "2 liters",
                isCompleted = true,
                createdAt = "2026-07-18T00:00:00Z",
                updatedAt = "2026-07-18T01:00:00Z",
            )

        val model = entity.toExternalModel()

        assertEquals(entity.id, model.id)
        assertEquals(entity.title, model.title)
        assertEquals(entity.description, model.description)
        assertEquals(entity.isCompleted, model.isCompleted)
        assertEquals(entity.createdAt, model.createdAt)
        assertEquals(entity.updatedAt, model.updatedAt)
    }
}
