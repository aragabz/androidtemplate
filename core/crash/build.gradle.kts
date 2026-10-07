plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "com.aragabz.androidtemplate.core.crash"
}

dependencies {
    implementation(project(":core:common"))
    // CrashReportingTree is a Timber.Tree, so Timber is part of this module's API.
    api(libs.timber)
}
