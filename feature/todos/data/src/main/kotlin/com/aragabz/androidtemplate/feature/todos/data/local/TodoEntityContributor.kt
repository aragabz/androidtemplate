package com.aragabz.androidtemplate.feature.todos.data.local

import com.aragabz.androidtemplate.core.common.util.EntityContributor
import com.aragabz.androidtemplate.feature.todos.data.local.entity.TodoEntity
import kotlin.reflect.KClass

class TodoEntityContributor : EntityContributor {
    override fun getEntities(): List<KClass<*>> = listOf(TodoEntity::class)
}
