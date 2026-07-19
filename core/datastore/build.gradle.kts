plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "com.aragabz.androidtemplate.core.datastore"
}

dependencies {
    api(project(":core:common"))

    implementation(libs.androidx.security.crypto)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.core)
}
