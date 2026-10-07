package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Configure base Kotlin with Android options
 * 
 * Note: Since AGP 9.0+, Kotlin support is built-in to Android Gradle Plugin.
 * The Kotlin version is automatically derived from the AGP version and doesn't need 
 * explicit Kotlin Android plugin application. The version is defined in libs.versions.toml
 * and applied through the Android Gradle Plugin itself.
 * 
 * For more details: https://kotl.in/gradle/agp-built-in-kotlin
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = 37

        defaultConfig.apply {
            minSdk = 26
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
            // Enable desugaring to support Java 8+ APIs on API 26+
            isCoreLibraryDesugaringEnabled = true
        }

        buildFeatures.apply {
            buildConfig = true
        }

        lint.apply {
            abortOnError = true
            xmlReport = true
            sarifReport = true
            checkDependencies = true
        }
    }

    dependencies {
        // Add desugaring dependency when enabled
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
        // The runner declared by testInstrumentationRunner; without it instrumentation crashes and reports 0 tests.
        add("androidTestImplementation", libs.findLibrary("androidx.test.runner").get())
        // compose-ui-test pulls Espresso 3.5, which crashes on API 35+; pin the catalog version.
        add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
        // Shared test helpers (MainDispatcherRule, fakes) plus JUnit, coroutines-test and Turbine via its api.
        if (path != ":core:testing") {
            add("testImplementation", project(":core:testing"))
        }
    }

    // Configure Kotlin compiler options
    // AGP 9.0+ automatically applies Kotlin based on the version in gradle/libs.versions.toml
    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    extensions.configure<KotlinJvmProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
