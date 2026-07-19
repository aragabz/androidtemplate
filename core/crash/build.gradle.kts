plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "com.aragabz.androidtemplate.core.crash"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.timber)

    testImplementation(libs.junit)
}
