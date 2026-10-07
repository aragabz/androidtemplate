package com.aragabz.androidtemplate.core.crash

import android.util.Log
import timber.log.Timber

/**
 * Release logging: forwards warnings and errors to [CrashReporter] instead of logcat.
 */
class CrashReportingTree(
    private val crashReporter: CrashReporter,
) : Timber.Tree() {
    override fun isLoggable(
        tag: String?,
        priority: Int,
    ): Boolean = priority >= Log.WARN && tag != TimberCrashReporter.TAG

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        crashReporter.logMessage(message)
        t?.let(crashReporter::logException)
    }
}
