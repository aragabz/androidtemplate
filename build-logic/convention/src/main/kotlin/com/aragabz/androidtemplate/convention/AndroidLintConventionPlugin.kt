package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply lint checks to all modules
            dependencies {
                add("lintChecks", project(":core:lint"))
            }
        }
    }
}
