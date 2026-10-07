plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aragabz.androidtemplate.core.network"

    defaultConfig {
        buildConfigField("boolean", "ENABLE_MOCK_INTERCEPTOR", "false")
    }

    buildTypes {
        debug {
            // Debug builds answer known endpoints with canned data; pass -PmockApi=false to use the real backend.
            val mockApi = providers
                .gradleProperty("mockApi")
                .orElse("true")
                .get()
                .toBoolean()
            buildConfigField("boolean", "ENABLE_MOCK_INTERCEPTOR", mockApi.toString())
        }
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

    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)
}
