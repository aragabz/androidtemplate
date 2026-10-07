// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.module.graph.assertion)
}

moduleGraphConfig {
    readmePath.set("./README.md")
    heading.set("## Module Graph")
    // Production graph from :app: skip test, lint-check and baseline-profile edges.
    rootModulesRegex.set(":app")
    excludedConfigurationsRegex.set(".*([Tt]est|lintChecks|baselineProfile).*")
}

// buildHealth reports dependency advice without failing the build: the remaining advice is mostly
// "declare transitive dependencies directly" and intentional convention-plugin dependencies
// (Espresso pin, test runner, :core:testing). Review build/reports/dependency-analysis/build-health-report.txt.
dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("warn")
            }
        }
    }
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
