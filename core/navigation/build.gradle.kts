plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.library.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aragabz.androidtemplate.core.navigation"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.serialization.json)
}
