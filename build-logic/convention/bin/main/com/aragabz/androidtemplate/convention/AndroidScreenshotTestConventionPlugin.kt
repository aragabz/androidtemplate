package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidScreenshotTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            // Apply it only when an Android plugin (Library or Application) is active
            plugins.withId("com.android.base") {
                pluginManager.apply("io.github.takahirom.roborazzi")
            }

            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension>("android") {
                    testOptions {
                        unitTests {
                            isIncludeAndroidResources = true
                        }
                    }
                }
            }

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension>("android") {
                    testOptions {
                        unitTests {
                            isIncludeAndroidResources = true
                        }
                    }
                }
            }

            dependencies {
                add("testImplementation", libs.findLibrary("roborazzi").get())
                add("testImplementation", libs.findLibrary("roborazzi-compose").get())
                add("testImplementation", libs.findLibrary("robolectric").get())
                add("testImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
            }
        }
    }
}
