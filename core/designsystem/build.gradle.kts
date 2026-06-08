plugins {
    id("androidtemplate.android.library.compose")
}

android {
    namespace = "com.aragabz.androidtemplate.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
