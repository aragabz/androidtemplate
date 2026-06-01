package com.aragabz.androidtemplate.feature.todos.di

import com.aragabz.androidtemplate.feature.todos.data.repository.TodosRepositoryImpl
import com.aragabz.androidtemplate.feature.todos.domain.repository.TodosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for todos data layer.
 */
@Module
@InstallIn(SingletonComponent::class)
interface TodosDataModule {
    @Binds
    fun bindTodosRepository(impl: TodosRepositoryImpl): TodosRepository
}
