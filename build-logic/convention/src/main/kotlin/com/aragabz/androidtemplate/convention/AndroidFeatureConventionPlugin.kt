package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("androidtemplate.android.library")
                apply("androidtemplate.android.library.compose")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig.apply {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                add("implementation", project(":core:ui"))
                // Removed project(":core:designsystem") as it might not exist or be renamed
                // add("implementation", project(":core:designsystem"))

                add("testImplementation", project(":core:testing"))
                add("androidTestImplementation", project(":core:testing"))
            }
        }
    }
}
