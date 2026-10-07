package com.aragabz.androidtemplate.baselineprofile

import androidx.test.platform.app.InstrumentationRegistry

/**
 * Application id of the variant under test (e.g. the `.dev` suffix for the dev flavor),
 * passed by the Baseline Profile Gradle plugin.
 */
internal val targetAppId: String
    get() =
        InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: error("targetAppId not passed as an instrumentation runner argument")
