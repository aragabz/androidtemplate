plugins {
    id("androidtemplate.jvm.library")
}

dependencies {
    api(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
}
