plugins {
    id("androidtemplate.android.library.compose")
    id("androidtemplate.android.screenshot.test")
}

android {
    namespace = "com.aragabz.androidtemplate.core.ui"
}

dependencies {
    // Core modules - expose design system to consumers
    api(project(":core:designsystem"))
    implementation(project(":core:common"))

    // Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test.junit4)
}
