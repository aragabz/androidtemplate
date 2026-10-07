plugins {
    id("androidtemplate.jvm.library")
    id("androidtemplate.android.hilt")
}

// Pure Kotlin/JVM: results, errors, dispatcher/scope qualifiers and their Hilt modules, the NetworkMonitor
// contract. Android-dependent code lives in the modules that own it (UiText in core:ui, the
// ConnectivityManager monitor in core:network).
dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
