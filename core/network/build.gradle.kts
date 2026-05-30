plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aragabz.androidtemplate.core.network"

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
    }
}

dependencies {
    api(project(":core:common"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
