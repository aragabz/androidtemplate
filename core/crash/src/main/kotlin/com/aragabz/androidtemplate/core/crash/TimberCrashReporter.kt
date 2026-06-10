package com.aragabz.androidtemplate.core.crash

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Timber implementation of [CrashReporter].
 * This is a fallback implementation that logs to Timber.
 * In a real app, you would swap this with Firebase Crashlytics or similar.
 */
@Singleton
class TimberCrashReporter @Inject constructor() : CrashReporter {
    override fun logException(throwable: Throwable) {
        Timber.e(throwable)
    }

    override fun logMessage(message: String) {
        Timber.d("CrashReport: $message")
    }

    override fun setCustomKey(key: String, value: Any) {
        Timber.d("CrashReport Key: $key = $value")
    }

    override fun setUserId(userId: String) {
        Timber.d("CrashReport UserID: $userId")
    }
}
