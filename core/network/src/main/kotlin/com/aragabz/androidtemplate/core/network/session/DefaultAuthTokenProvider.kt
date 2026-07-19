package com.aragabz.androidtemplate.core.network.session

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthTokenProvider
    @Inject
    constructor() : AuthTokenProvider {
        override fun getAuthToken(): String? = null
    }
