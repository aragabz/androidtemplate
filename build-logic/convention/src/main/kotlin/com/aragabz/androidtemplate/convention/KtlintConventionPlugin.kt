package com.aragabz.androidtemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Ktlint convention plugin for consistent code formatting across all modules.
 */
class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            
            // Configuration is handled via .editorconfig and .ktlintrc files
        }
    }
}
