package com.aragabz.androidtemplate.convention

import com.aragabz.androidtemplate.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Hilt dependency injection setup.
 * Applies Hilt and KSP plugins, adds necessary dependencies.
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            dependencies {
                add("implementation", libs.findLibrary("hilt.android").get())
                add("ksp", libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}
