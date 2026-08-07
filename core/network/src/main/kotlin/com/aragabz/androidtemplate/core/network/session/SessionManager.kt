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
        private val _onUnauthorized = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

        /**
         * SharedFlow that emits when a 401 Unauthorized response is received.
         * Collect this in your app (e.g., MainActivity) with lifecycle scope to handle logout/navigation.
         */
        val onUnauthorized: SharedFlow<Unit> = _onUnauthorized.asSharedFlow()

        /**
         * Call this when a 401 response is detected to notify all collectors.
         * Non-suspending — uses tryEmit for fire-and-forget notification.
         * Actual handling (navigation, logout) should happen in lifecycle-scoped collectors.
         */
        fun notifyUnauthorized() {
            _onUnauthorized.tryEmit(Unit)
        }
    }
