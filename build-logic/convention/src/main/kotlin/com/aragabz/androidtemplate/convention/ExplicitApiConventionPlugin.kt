package com.aragabz.androidtemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

/**
 * Convention plugin that enables explicit API mode for core modules.
 * 
 * Explicit API mode enforces:
 * - All public/protected declarations must have explicit visibility modifiers
 * - All public/protected declarations must have explicit return types
 * - Prevents accidental API exposure
 * 
 * Apply to core modules that expose public APIs to features.
 */
class ExplicitApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // This plugin should be applied after kotlin plugin
            pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                configureExplicitApi()
            }
            
            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                configureExplicitApi()
            }
        }
    }
    
    private fun Project.configureExplicitApi() {
        extensions.configure<KotlinProjectExtension> {
            // Strict mode: All public APIs must be explicitly declared
            explicitApi()
            
            // Alternative: Warning mode (use during migration)
            // explicitApiWarning()
        }
    }
}
