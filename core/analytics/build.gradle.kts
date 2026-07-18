plugins {
    id("androidtemplate.android.library")
}

android {
    namespace = "com.aragabz.androidtemplate.core.analytics"
}

dependencies {
    implementation(project(":core:common"))
}
