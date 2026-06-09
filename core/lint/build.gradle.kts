plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)
    
    testImplementation(libs.android.lint.tests)
}
