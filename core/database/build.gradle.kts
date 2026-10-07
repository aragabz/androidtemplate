plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.room")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aragabz.androidtemplate.core.database"
}

dependencies {
    api(project(":core:common"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.core)
}
