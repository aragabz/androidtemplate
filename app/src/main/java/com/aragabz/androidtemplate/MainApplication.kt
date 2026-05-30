package com.aragabz.androidtemplate

import android.app.Application
import com.aragabz.androidtemplate.core.network.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class with Hilt initialization.
 */
@HiltAndroidApp
class MainApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
