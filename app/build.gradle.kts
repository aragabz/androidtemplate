plugins {
    id("androidtemplate.android.application.compose")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.room")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.aragabz.androidtemplate"

    defaultConfig {
        applicationId = "com.aragabz.androidtemplate"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    baselineProfile(project(":baselineprofile"))

    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:sync"))
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(project(":core:ui")) // Exposes designsystem transitively
    implementation(project(":core:navigation"))

    // Feature modules
    implementation(project(":feature:home"))
    implementation(project(":feature:todos:ui"))
    implementation(project(":feature:todos:data"))

    // Navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.hilt.navigation.compose)

    // Logging
    implementation(libs.timber)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
