plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.kotlin.explicit.api")
}

android {
    namespace = "com.aragabz.androidtemplate.core.flags"
}

dependencies {
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
