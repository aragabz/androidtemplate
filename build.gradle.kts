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

// Apply ktlint and detekt to all subprojects
subprojects {
    // Intentionally empty to troubleshoot sync issues
}

tasks.register("detektAll") {
    description = "Run detekt on all modules"
    group = "verification"
    
    dependsOn(subprojects.map { "${it.path}:detekt" })
}
