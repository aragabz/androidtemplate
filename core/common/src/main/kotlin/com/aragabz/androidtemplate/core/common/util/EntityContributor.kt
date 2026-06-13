package com.aragabz.androidtemplate.core.common.util

import kotlin.reflect.KClass

/**
 * Interface for contributing entities to the database.
 */
interface EntityContributor {
    fun getEntities(): List<KClass<*>>
}
