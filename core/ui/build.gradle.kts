plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.library.compose")
}

android {
    namespace = "com.aragabz.androidtemplate.core.ui"
}

dependencies {
    // Core modules - expose designsystem to consumers
    api(project(":core:designsystem"))
    implementation(project(":core:common"))

    // Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
