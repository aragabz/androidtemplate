plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.screenshot.test")
}

android {
    namespace = "com.aragabz.androidtemplate.feature.auth.ui"
}

dependencies {
    implementation(project(":feature:auth:domain"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:datastore"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
