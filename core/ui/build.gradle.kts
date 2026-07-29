plugins {
    id("androidtemplate.android.library.compose")
    id("androidtemplate.android.screenshot.test")
    id("androidtemplate.kotlin.explicit.api")
}

android {
    namespace = "com.aragabz.androidtemplate.core.ui"
}

dependencies {
    // Core modules - expose design system to consumers
    api(project(":core:designsystem"))
    implementation(project(":core:common"))

    // Compose
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil)

    testImplementation(libs.junit)
}
