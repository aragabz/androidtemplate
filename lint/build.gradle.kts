plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dependency.analysis)
}

dependencies {
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)

    testImplementation(libs.android.lint.api)
    testImplementation(libs.android.lint.tests)
    // LintDetectorTest extends junit.framework.TestCase (dependency-analysis reports this as unused).
    testImplementation(libs.junit)
}
