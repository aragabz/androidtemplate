# Build Logic — Convention Plugins

The `build-logic/` directory contains Gradle convention plugins that standardize build configuration across all modules.

## Available Plugins

| Plugin ID | Applies To | What It Does |
|-----------|-----------|--------------|
| `androidtemplate.android.application` | App module | Compile SDK 36, min SDK 24, Java 11, Kotlin Android |
| `androidtemplate.android.application.compose` | App module | Enables Compose, applies Compose Compiler plugin, adds BOM |
| `androidtemplate.android.library` | Library modules | Same base config as application |
| `androidtemplate.android.library.compose` | Library modules | Compose support for libraries |
| `androidtemplate.android.feature` | Feature modules | Combines library + compose + common feature dependencies |
| `androidtemplate.android.hilt` | Any module | Applies Hilt plugin + KSP + Hilt dependencies |
| `androidtemplate.android.room` | Database modules | Room plugin + KSP + Room dependencies + schema export |

## Usage Examples

```kotlin
// Feature module
plugins {
    id("androidtemplate.android.feature")
}

// Core library with Compose
plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.library.compose")
}

// Module with Hilt DI
plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
}
```

## Structure

```
build-logic/
├── convention/
│   ├── src/main/kotlin/
│   │   ├── AndroidApplicationConventionPlugin.kt
│   │   ├── AndroidApplicationComposeConventionPlugin.kt
│   │   ├── AndroidLibraryConventionPlugin.kt
│   │   ├── AndroidLibraryComposeConventionPlugin.kt
│   │   ├── AndroidFeatureConventionPlugin.kt
│   │   ├── AndroidHiltConventionPlugin.kt
│   │   ├── AndroidRoomConventionPlugin.kt
│   │   └── com/aragabz/androidtemplate/convention/
│   │       ├── AndroidCompose.kt        # Shared Compose configuration
│   │       ├── KotlinAndroid.kt         # Shared Kotlin/Android configuration
│   │       └── ProjectExtensions.kt     # Version catalog helpers
│   └── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Modifying Plugins

1. Edit the plugin file in `build-logic/convention/src/main/kotlin/`
2. Changes propagate to all modules using that plugin automatically
3. Run `./gradlew tasks` to verify the build still works

## Adding a New Plugin

1. Create a new `*ConventionPlugin.kt` in the convention source directory
2. Register it in `build-logic/convention/build.gradle.kts` under `gradlePlugin.plugins`
3. Apply it in target modules with `id("androidtemplate.android.<name>")`

## Benefits

- **DRY** — Configuration defined once, used everywhere
- **Consistency** — All modules share the same baseline
- **Type Safety** — Written in Kotlin with full IDE support
- **Maintainability** — SDK version bumps happen in one place
