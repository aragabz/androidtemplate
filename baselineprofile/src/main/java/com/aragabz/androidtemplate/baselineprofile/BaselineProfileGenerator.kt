package com.aragabz.androidtemplate.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic baseline profile for the target package.
 * It will visit the main screens of the app to ensure that their code is optimized.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(
            packageName = targetAppId,
            // Check out docs for more attribution options:
            // https://developer.android.com/topic/performance/baselineprofiles/test-baseline-profiles#attribution
            includeInStartupProfile = true,
        ) {
            // This block defines the app's critical user journey to be optimized.
            // For now, we just start the app and wait for it to be displayed.
            pressHome()
            startActivityAndWait()

            // You can add more interactions here to optimize other parts of the app:
            // - Scroll through lists
            // - Navigate to different screens
            // - Perform common actions
        }
}
