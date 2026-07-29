plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.kotlin.explicit.api")
}

android {
    namespace = "com.aragabz.androidtemplate.core.domain"
}

dependencies {
    api(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
