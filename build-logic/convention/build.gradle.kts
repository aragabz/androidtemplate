import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.tasks.testing.Test

plugins {
    `kotlin-dsl`
}

group = "com.aragabz.androidtemplate.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.metadata.jvm)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.room.gradlePlugin)
    implementation(libs.roborazzi.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    // Ktlint plugin is applied via plugin portal, not as a classpath dependency
}

tasks.withType<Test>().configureEach {
    reports.junitXml.required.set(true)
    reports.html.required.set(true)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "androidtemplate.android.application"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "androidtemplate.android.application.compose"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "androidtemplate.android.library"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "androidtemplate.android.library.compose"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidLibraryComposeConventionPlugin"
        }
        register("androidTest") {
            id = "androidtemplate.android.test"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidTestConventionPlugin"
        }
        register("androidFeature") {
            id = "androidtemplate.android.feature"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "androidtemplate.jvm.library"
            implementationClass = "com.aragabz.androidtemplate.convention.JvmLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "androidtemplate.android.hilt"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "androidtemplate.android.room"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidRoomConventionPlugin"
        }
        register("androidScreenshotTest") {
            id = "androidtemplate.android.screenshot.test"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidScreenshotTestConventionPlugin"
        }
        register("androidLint") {
            id = "androidtemplate.android.lint"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidLintConventionPlugin"
        }
        register("detekt") {
            id = "androidtemplate.detekt"
            implementationClass = "com.aragabz.androidtemplate.convention.DetektConventionPlugin"
        }
        register("ktlint") {
            id = "androidtemplate.ktlint"
            implementationClass = "com.aragabz.androidtemplate.convention.KtlintConventionPlugin"
        }
    }
}
