package com.aragabz.androidtemplate.core.network.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user session and handles unauthorized (401) responses globally.
 */
@Singleton
class SessionManager
    @Inject
    constructor() {
        private val _onUnauthorized = MutableSharedFlow<Unit>(replay = 0)

        /**
         * SharedFlow that emits when a 401 Unauthorized response is received.
         * Collect this in your app to handle logout/navigation to login.
         */
        val onUnauthorized: SharedFlow<Unit> = _onUnauthorized.asSharedFlow()

        /**
         * Call this when a 401 response is detected to notify all collectors.
         */
        suspend fun notifyUnauthorized() {
            _onUnauthorized.emit(Unit)
        }
    }
