plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "com.aragabz.androidtemplate.feature.profile.ui"
}

dependencies {
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:data"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
