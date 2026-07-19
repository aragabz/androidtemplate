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
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.module.graph)
    alias(libs.plugins.module.graph.assertion)
}

// Configure Kotlin version for all subprojects
subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
        configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
            // Kotlin options are configured in convention plugins
        }
    }
}

moduleGraphConfig {
    readmePath.set("./README.md")
    heading.set("## Module Graph")
}

moduleGraphAssert {
    // Basic architecture rules: features should not depend on other features
    // and should only depend on core modules.
    allowed = arrayOf(
        ":feature:.* -> :core:.*",
        ":app -> :feature:.*",
        ":app -> :core:.*",
        ":core:.* -> :core:.*"
    )
    maxHeight = 4
}

// Apply dependency-analysis to all subprojects
subprojects {
    apply(plugin = "com.autonomousapps.dependency-analysis")
}

tasks.register("detektAll") {
    description = "Run detekt on all modules"
    group = "verification"
    
    dependsOn(subprojects.map { "${it.path}:detekt" })
}
