plugins {
    id("androidtemplate.jvm.library")
}

dependencies {
    api(project(":core:common"))
    api(project(":core:domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
