plugins {
    id("androidtemplate.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aragabz.androidtemplate.feature.home"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
}
