plugins {
    id("androidtemplate.android.library")
}

android {
    namespace = "com.aragabz.androidtemplate.feature.settings.domain"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:domain"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
}
