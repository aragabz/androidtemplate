package com.aragabz.androidtemplate.core.common.di

import javax.inject.Qualifier

/**
 * Qualifier for IO dispatcher used for I/O operations like network requests and database queries.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
public annotation class IoDispatcher

/**
 * Qualifier for Main dispatcher used for UI operations.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
public annotation class MainDispatcher

/**
 * Qualifier for Default dispatcher used for CPU-intensive operations.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
public annotation class DefaultDispatcher
