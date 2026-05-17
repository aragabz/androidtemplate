import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
    implementation(libs.compose.gradlePlugin)
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
        register("androidFeature") {
            id = "androidtemplate.android.feature"
            implementationClass = "com.aragabz.androidtemplate.convention.AndroidFeatureConventionPlugin"
        }
    }
}
