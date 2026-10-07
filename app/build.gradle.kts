
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    id("androidtemplate.android.application.compose")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.room")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.baselineprofile)
}

// Release signing secrets come from Gradle properties or, as Fastlane passes them, environment variables.
fun releaseSecret(name: String): String? =
    providers.gradleProperty(name).orElse(providers.environmentVariable(name)).orNull

val releaseStoreFilePath = releaseSecret("RELEASE_STORE_FILE")
val releaseStorePassword = releaseSecret("RELEASE_STORE_PASSWORD")
val releaseKeyAlias = releaseSecret("RELEASE_KEY_ALIAS")
val releaseKeyPassword = releaseSecret("RELEASE_KEY_PASSWORD")
val hasReleaseSigningConfig =
    !releaseStoreFilePath.isNullOrBlank() &&
        !releaseStorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank()

android {
    namespace = "com.aragabz.androidtemplate"

    defaultConfig {
        applicationId = "com.aragabz.androidtemplate"
        // Defaults live in gradle.properties; CI/release lanes pass -PVERSION_CODE=<n> -PVERSION_NAME=<x.y.z>.
        versionCode = providers
            .gradleProperty("VERSION_CODE")
            .orElse("1")
            .get()
            .toInt()
        versionName = providers.gradleProperty("VERSION_NAME").orElse("1.0").get()
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            // Replace the placeholder API with each environment's backend.
            buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://jsonplaceholder.typicode.com/\"")
        }
    }

    signingConfigs {
        create("release") {
            if (!releaseStoreFilePath.isNullOrBlank()) {
                storeFile = file(releaseStoreFilePath)
            }
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Without release secrets the build is left unsigned, so packaging can still be validated
            // but a debug-signed artifact can never be shipped by mistake.
            signingConfig = if (hasReleaseSigningConfig) signingConfigs.getByName("release") else null
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }

    // Declares the app's languages (from its translated resources) for the per-app language setting.
    androidResources {
        generateLocaleConfig = true
    }
}

dependencies {
    baselineProfile(project(":baselineprofile"))

    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:analytics"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:sync"))
    implementation(project(":core:crash"))
    implementation(project(":core:flags"))
    implementation(libs.androidx.hilt.work)
    // The manifest edits androidx.startup's provider to disable WorkManager's default initializer.
    implementation(libs.androidx.startup.runtime)
    ksp(libs.androidx.hilt.compiler)
    implementation(project(":core:ui")) // Exposes designsystem transitively

    // Feature modules
    implementation(project(":feature:home"))
    implementation(project(":feature:todos:ui"))
    implementation(project(":feature:todos:data"))
    implementation(project(":feature:auth:ui"))
    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:data"))
    implementation(project(":feature:profile:ui"))
    implementation(project(":feature:profile:data"))
    implementation(project(":feature:settings:ui"))
    implementation(project(":feature:settings:data"))

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Logging
    implementation(libs.timber)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.room.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.register("verifyDebugCoverage") {
    group = "verification"
    description = "Verifies debug unit test line coverage against COVERAGE_MIN_LINE and writes a summary output."
    dependsOn("createDevDebugUnitTestCoverageReport")

    val reportFileProvider = layout.buildDirectory.file("reports/coverage/test/dev/debug/report.xml")
    val summaryFileProvider = layout.buildDirectory.file("reports/coverage/test/dev/debug/coverage-summary.txt")
    val minimumCoverageProvider = providers.gradleProperty("COVERAGE_MIN_LINE")

    inputs.file(reportFileProvider)
    inputs.property("minimumLineCoverage", minimumCoverageProvider)
    outputs.file(summaryFileProvider)

    doLast {
        val reportFile = reportFileProvider.get().asFile
        if (!reportFile.exists()) {
            throw GradleException(
                "Coverage report not found at ${reportFile.path}. Run createDevDebugUnitTestCoverageReport first.",
            )
        }

        val dbFactory =
            DocumentBuilderFactory.newInstance().apply {
                setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                setFeature("http://xml.org/sax/features/external-general-entities", false)
                setFeature("http://xml.org/sax/features/external-parameter-entities", false)
                isXIncludeAware = false
                isExpandEntityReferences = false
            }

        val builder =
            dbFactory.newDocumentBuilder().apply {
                setEntityResolver { _, _ -> InputSource(StringReader("")) }
            }

        val document = builder.parse(reportFile)
        val counters = document.getElementsByTagName("counter")
        var lineMissed = 0
        var lineCovered = 0

        for (index in 0 until counters.length) {
            val node = counters.item(index)
            if (node is Element && node.getAttribute("type") == "LINE") {
                lineMissed += node.getAttribute("missed").toInt()
                lineCovered += node.getAttribute("covered").toInt()
            }
        }

        val totalLines = lineMissed + lineCovered
        val coverageRatio = if (totalLines == 0) 0.0 else lineCovered.toDouble() / totalLines.toDouble()
        val minimumRatio = (inputs.properties["minimumLineCoverage"] as String).toDouble()
        val coveragePercent = coverageRatio * 100
        val minimumPercent = minimumRatio * 100

        val summaryFile = summaryFileProvider.get().asFile
        summaryFile.parentFile.mkdirs()
        summaryFile.writeText(
            """
            lineCoveragePercent=${String.format(Locale.US, "%.2f", coveragePercent)}
            minimumRequiredPercent=${String.format(Locale.US, "%.2f", minimumPercent)}
            coveredLines=$lineCovered
            missedLines=$lineMissed
            totalLines=$totalLines
            """.trimIndent() + "\n",
        )

        this.logger.lifecycle(
            "Debug unit test coverage: ${String.format(
                Locale.US,
                "%.2f",
                coveragePercent,
            )}% (min ${String.format(Locale.US, "%.2f", minimumPercent)}%)",
        )

        if (coverageRatio < minimumRatio) {
            throw GradleException(
                "Coverage check failed: ${String.format(
                    Locale.US,
                    "%.2f",
                    coveragePercent,
                )}% < ${String.format(Locale.US, "%.2f", minimumPercent)}%",
            )
        }
    }
}

tasks.named("check") {
    dependsOn("verifyDebugCoverage")
}
