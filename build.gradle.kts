// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

// Apply ktlint to all subprojects
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.0.1")
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        
        filter {
            exclude("**/generated/**")
            exclude("**/build/**")
        }
    }
}

// Apply detekt to root project only - it will check all modules
apply(plugin = "io.gitlab.arturbosch.detekt")

tasks.register("detektAll") {
    description = "Run detekt on all modules"
    group = "verification"
    
    dependsOn(subprojects.map { "${it.path}:detekt" })
}
