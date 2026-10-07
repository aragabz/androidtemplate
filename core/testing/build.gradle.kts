plugins {
    id("androidtemplate.android.library")
}

android {
    namespace = "com.aragabz.androidtemplate.core.testing"
}

// Shared unit-test helpers. The Android convention plugins add this module, JUnit, coroutines-test and Turbine
// to every other module's testImplementation, so consumers never declare them.
dependencies {
    api(project(":core:datastore"))
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
}
