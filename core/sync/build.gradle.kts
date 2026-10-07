plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "com.aragabz.androidtemplate.core.sync"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:domain"))
    implementation(project(":core:datastore"))

    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.android)
    implementation(libs.timber)
    ksp(libs.hilt.compiler)

    // Hilt Worker
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
