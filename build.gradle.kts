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
    // Features depend only on core modules; within a feature, ui and data depend on domain.
    allowed = arrayOf(
        ":feature:.* -> :core:.*",
        ":feature:(\\w+):(ui|data) -> :feature:\\1:domain",
        ":app -> :feature:.*",
        ":app -> :core:.*",
        ":core:.* -> :core:.*",
    )
    maxHeight = 4
}

// Apply dependency-analysis to all subprojects
subprojects {
    apply(plugin = "com.autonomousapps.dependency-analysis")
}
