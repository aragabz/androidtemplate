package com.aragabz.androidtemplate.convention

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * Single source for the SDK levels and Java/JVM target shared by every Android module.
 */
internal object AndroidSdk {
    const val COMPILE = 37
    const val MIN = 26
    const val TARGET = 36
    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
    val javaVersion: JavaVersion = JavaVersion.VERSION_21
    val jvmTarget: JvmTarget = JvmTarget.JVM_21
}
