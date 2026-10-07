package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        buildFeatures.compose = true
    }

    // Compose compiler metrics/reports are opt-in: ./gradlew <task> -PcomposeCompilerReports=true
    // writes them to <module>/build/compose_compiler.
    val composeCompilerReports =
        providers.gradleProperty("composeCompilerReports").map(String::toBoolean).getOrElse(false)
    if (composeCompilerReports) {
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            val outputDir = layout.buildDirectory.dir("compose_compiler")
            reportsDestination.set(outputDir)
            metricsDestination.set(outputDir)
        }
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))

        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-compose-material3").get())
        add("implementation", libs.findLibrary("androidx-compose-material-icons-extended").get())
    }
}
