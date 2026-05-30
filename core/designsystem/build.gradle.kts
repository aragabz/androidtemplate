plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.library.compose")
}

android {
    namespace = "com.aragabz.androidtemplate.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
