plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "com.aragabz.androidtemplate.feature.settings.ui"
}

dependencies {
    implementation(project(":feature:settings:domain"))
    implementation(project(":feature:settings:data"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
