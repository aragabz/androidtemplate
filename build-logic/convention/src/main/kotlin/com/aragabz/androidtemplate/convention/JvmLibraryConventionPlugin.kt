package com.aragabz.androidtemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for pure Kotlin/JVM modules (`core:common`, `core:domain`, `feature:*:domain`).
 * Shares the Java/JVM target with the Android conventions and applies the same lint, detekt, ktlint and
 * dependency-analysis setup. `:core:testing` is an Android library, so its JVM-compatible parts
 * (JUnit, coroutines-test, Turbine) are added directly.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                // Android lint for JVM modules, so :app's lint (checkDependencies) covers them too.
                apply("com.android.lint")
                apply("androidtemplate.android.lint")
                apply("androidtemplate.detekt")
                apply("androidtemplate.ktlint")
                apply("com.autonomousapps.dependency-analysis")
            }

            configureKotlinJvm()

            dependencies {
                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
                add("testImplementation", libs.findLibrary("turbine").get())
            }

            // JVM modules only have `test`; alias the Android task name so `./gradlew testDebugUnitTest`
            // (CI and docs) runs every module's unit tests.
            tasks.register("testDebugUnitTest") {
                group = "verification"
                description = "Runs the unit tests (alias of test, matching the Android modules' task name)."
                dependsOn("test")
            }
        }
    }
}
