plugins {
    id("androidtemplate.android.library.compose")
    id("androidtemplate.android.screenshot.test")
}

android {
    namespace = "com.aragabz.androidtemplate.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
