plugins {
    id("androidtemplate.android.library.compose")
    id("androidtemplate.android.hilt")
    id("androidtemplate.kotlin.explicit.api")
}

android {
    namespace = "com.aragabz.androidtemplate.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)
    implementation(libs.androidx.biometric)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
