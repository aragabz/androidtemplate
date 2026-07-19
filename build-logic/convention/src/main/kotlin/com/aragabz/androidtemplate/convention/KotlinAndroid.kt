package com.aragabz.androidtemplate.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
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
        }

        buildFeatures.apply {
            buildConfig = true
        }

        lint.apply {
            abortOnError = false
            xmlReport = true
            sarifReport = true
            checkDependencies = true
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
