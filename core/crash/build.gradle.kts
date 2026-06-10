plugins {
    id("androidtemplate.android.library")
}

android {
    namespace = "com.aragabz.androidtemplate.core.crash"
}

dependencies {
    implementation(libs.timber)
}
