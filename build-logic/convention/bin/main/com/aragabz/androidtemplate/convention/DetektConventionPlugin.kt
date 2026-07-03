package com.aragabz.androidtemplate.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                toolVersion = "1.23.8"
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
                allRules = false
                autoCorrect = false
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    sarif.required.set(true)
                }
            }
        }
    }
}
