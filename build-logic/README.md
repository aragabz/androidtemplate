# Build Logic

`build-logic` is an included build (see `pluginManagement { includeBuild("build-logic") }` in the root
`settings.gradle.kts`) that provides the project's convention plugins. Module build files only apply these
plugins, set `namespace` and declare dependencies; shared Android, Kotlin and Compose settings live here.
Plugin and library versions come from `gradle/libs.versions.toml`.

## Shared values

`AndroidSdk.kt` is the single source for:

| Setting | Value |
| --- | --- |
| `compileSdk` | 37 |
| `minSdk` | 26 |
| `targetSdk` (app and test modules) | 36 |
| Java source/target compatibility, Kotlin `jvmTarget` | 21 |
| Instrumentation runner | `androidx.test.runner.AndroidJUnitRunner` |

Core library desugaring is enabled for every Android app and library module. Pure Kotlin/JVM modules use the same
Java/JVM target (`configureKotlinJvm`).

**Kotlin.** AGP 9 has built-in Kotlin support, so no module applies `org.jetbrains.kotlin.android`. The
Kotlin Gradle plugin (version `kotlin` in the catalog) is on the build-logic classpath and AGP uses it for
compilation; `configureKotlinAndroid` only sets the JVM target. Pure Kotlin/JVM library modules (`core:common`,
`core:domain`, `feature:*:domain`) apply `androidtemplate.jvm.library`. The `:lint` module (lint checks, not a
library) applies `org.jetbrains.kotlin.jvm` through the catalog alias `libs.plugins.kotlin.jvm`.

## Plugins

| Id | Applies / configures |
| --- | --- |
| `androidtemplate.android.application` | `com.android.application`, shared SDK/Java config, `targetSdk`, lint, detekt, ktlint, dependency-analysis |
| `androidtemplate.android.application.compose` | the above plus the Compose compiler plugin and Compose BOM/material3 dependencies |
| `androidtemplate.android.library` | `com.android.library`, shared SDK/Java config, lint, detekt, ktlint, dependency-analysis |
| `androidtemplate.android.library.compose` | the above plus Compose |
| `androidtemplate.android.feature` | `androidtemplate.android.library.compose` plus `:core:ui` |
| `androidtemplate.jvm.library` | `org.jetbrains.kotlin.jvm`, shared Java/JVM target, Android lint (`com.android.lint` + custom checks), detekt, ktlint, dependency-analysis, JUnit/coroutines-test/Turbine for tests, and a `testDebugUnitTest` alias of `test` |
| `androidtemplate.android.test` | `com.android.test` modules (`:baselineprofile`): shared SDK levels, Java/JVM target, runner, detekt, ktlint, dependency-analysis |
| `androidtemplate.android.hilt` | KSP and the Hilt compiler; on Android modules also the Hilt plugin and `hilt-android`, on JVM modules `hilt-core` (for `@Module`/`@InstallIn` code such as `core:common`'s dispatcher modules) |
| `androidtemplate.android.room` | Room and KSP plugins, Room runtime/compiler, schema export to `<module>/schemas` |
| `androidtemplate.android.screenshot.test` | Roborazzi plugin, Robolectric/Roborazzi test dependencies, Android resources in unit tests |
| `androidtemplate.android.lint` | adds the custom checks from `:lint` (`lintChecks`) |
| `androidtemplate.detekt` | detekt with `config/detekt/detekt.yml` (HTML, XML and SARIF reports) |
| `androidtemplate.ktlint` | ktlint Gradle plugin; rules come from the root `.editorconfig` |

Every Android app/library module also gets `:core:testing` (JUnit, coroutines-test, Turbine, `MainDispatcherRule`) as
a `testImplementation` dependency, plus the AndroidX test runner and a pinned Espresso for instrumented tests. JVM
modules can't use the Android `:core:testing`, so they get JUnit, coroutines-test and Turbine directly.

**Compose compiler reports.** Metrics and reports are off by default. Pass `-PcomposeCompilerReports=true`
to any build to write them to `<module>/build/compose_compiler`.

## Structure

```
build-logic/
├── convention/
│   ├── build.gradle.kts            # plugin registrations (gradlePlugin { plugins { ... } })
│   └── src/main/kotlin/com/aragabz/androidtemplate/convention/
│       ├── AndroidSdk.kt           # SDK levels and Java/JVM target
│       ├── KotlinAndroid.kt        # configureKotlinAndroid() for app/library plugins, configureKotlinJvm() for JVM modules
│       ├── AndroidCompose.kt       # configureAndroidCompose()
│       ├── ProjectExtensions.kt    # Project.libs (version catalog access)
│       └── *ConventionPlugin.kt    # one class per plugin id above
├── gradle.properties
└── settings.gradle.kts             # reuses ../gradle/libs.versions.toml
```

## Adding a convention plugin

1. Add a `Plugin<Project>` class in `convention/src/main/kotlin/com/aragabz/androidtemplate/convention/`.
2. Register it in `convention/build.gradle.kts` under `gradlePlugin.plugins` with an `androidtemplate.*` id.
3. Apply it in a module with `id("androidtemplate.<name>")`.
