package com.aragabz.androidtemplate.core.network.di

import javax.inject.Qualifier

/**
 * Qualifies the API base URL. Provided by :app, which knows the build flavor's backend.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl
