plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.screenshot.test")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.aragabz.androidtemplate.feature.home"
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "FEATURE_ENV", "\"${providers.gradleProperty("DEFAULT_API_ENV").orElse("dev").get()}\"")
    }
}

dependencies {
    // Core modules
    api(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:navigation"))

    // Feature modules
    implementation(project(":feature:todos:ui"))
    implementation(project(":feature:auth:ui"))
    implementation(project(":feature:profile:ui"))
    implementation(project(":feature:settings:ui"))

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
