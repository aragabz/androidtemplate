
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


val releaseStoreFilePath = providers.gradleProperty("RELEASE_STORE_FILE").orNull
val releaseStorePassword = providers.gradleProperty("RELEASE_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.gradleProperty("RELEASE_KEY_ALIAS").orNull
val releaseKeyPassword = providers.gradleProperty("RELEASE_KEY_PASSWORD").orNull
val minimumLineCoverage = providers.gradleProperty("COVERAGE_MIN_LINE").orElse("0.00").map(String::toDouble)
val hasReleaseSigningConfig = !releaseStoreFilePath.isNullOrBlank() &&
    !releaseStorePassword.isNullOrBlank() &&
    !releaseKeyAlias.isNullOrBlank() &&
    !releaseKeyPassword.isNullOrBlank()

android {
    namespace = "com.aragabz.androidtemplate"

    defaultConfig {
        applicationId = "com.aragabz.androidtemplate"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_ENV", "\"${providers.gradleProperty("DEFAULT_API_ENV").orElse("dev").get()}\"")
        buildConfigField(
            "boolean",
            "ANALYTICS_ENABLED",
            providers.gradleProperty("ENABLE_ANALYTICS_IN_DEBUG").orElse("false").get(),
        )
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_ENV", "\"dev\"")
            buildConfigField("boolean", "ANALYTICS_ENABLED", "false")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "API_ENV", "\"staging\"")
            buildConfigField("boolean", "ANALYTICS_ENABLED", "true")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_ENV", "\"prod\"")
            buildConfigField("boolean", "ANALYTICS_ENABLED", "true")
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
            // CI and local builds without release secrets can still validate packaging.
            signingConfig = if (hasReleaseSigningConfig) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
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
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(project(":core:ui")) // Exposes designsystem transitively
    implementation(project(":core:navigation"))

    // Feature modules
    implementation(project(":feature:home"))
    implementation(project(":feature:todos:ui"))
    implementation(project(":feature:todos:data"))

    // Navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.core.splashscreen)

    // Logging
    implementation(libs.timber)

    // Security
    implementation(libs.androidx.security.crypto)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Development and test network mocking
    debugImplementation(libs.okhttp.mockwebserver)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.room.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.register("verifyDebugCoverage") {
    group = "verification"
    description = "Verifies debug unit test line coverage against COVERAGE_MIN_LINE and writes a summary output."
    dependsOn("createDevDebugUnitTestCoverageReport")

    val reportFileProvider = layout.buildDirectory.file("reports/coverage/test/dev/debug/report.xml")
    val summaryFileProvider = layout.buildDirectory.file("reports/coverage/test/dev/debug/coverage-summary.txt")
    val minimumCoverageProvider = providers.gradleProperty("COVERAGE_MIN_LINE").orElse("0.00")

    inputs.file(reportFileProvider)
    inputs.property("minimumLineCoverage", minimumCoverageProvider)
    outputs.file(summaryFileProvider)

    doLast {
        val reportFile = reportFileProvider.get().asFile
        if (!reportFile.exists()) {
            throw GradleException("Coverage report not found at ${reportFile.path}. Run createDevDebugUnitTestCoverageReport first.")
        }

        val dbFactory = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            isXIncludeAware = false
            isExpandEntityReferences = false
        }

        val builder = dbFactory.newDocumentBuilder().apply {
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

        this.logger.lifecycle("Debug unit test coverage: ${String.format(Locale.US, "%.2f", coveragePercent)}% (min ${String.format(Locale.US, "%.2f", minimumPercent)}%)")

        if (coverageRatio < minimumRatio) {
            throw GradleException(
                "Coverage check failed: ${String.format(Locale.US, "%.2f", coveragePercent)}% < ${String.format(Locale.US, "%.2f", minimumPercent)}%",
            )
        }
    }
}

tasks.named("check") {
    dependsOn("verifyDebugCoverage")
}
