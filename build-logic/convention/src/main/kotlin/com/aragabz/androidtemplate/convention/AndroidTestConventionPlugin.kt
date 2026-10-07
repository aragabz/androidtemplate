package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for `com.android.test` modules (e.g. :baselineprofile).
 * Shares the SDK levels and Java/JVM target with the app and library conventions, plus detekt and ktlint.
 */
class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.test")
                apply("androidtemplate.detekt")
                apply("androidtemplate.ktlint")
                apply("com.autonomousapps.dependency-analysis")
            }

            extensions.configure<TestExtension> {
                compileSdk = AndroidSdk.COMPILE

                defaultConfig.apply {
                    minSdk = AndroidSdk.MIN
                    targetSdk = AndroidSdk.TARGET
                    testInstrumentationRunner = AndroidSdk.TEST_INSTRUMENTATION_RUNNER
                }

                compileOptions.apply {
                    sourceCompatibility = AndroidSdk.javaVersion
                    targetCompatibility = AndroidSdk.javaVersion
                }
            }

            configureKotlinAndroidJvmTarget()
        }
    }
}
