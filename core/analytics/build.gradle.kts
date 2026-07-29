plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.kotlin.explicit.api")
}

android {
    namespace = "com.aragabz.androidtemplate.core.analytics"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.timber)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
}
