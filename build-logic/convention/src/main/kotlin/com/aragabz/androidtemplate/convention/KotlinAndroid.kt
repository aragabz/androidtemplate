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
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig.apply {
            minSdk = 26
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        buildFeatures.apply {
            buildConfig = true
        }
    }

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
