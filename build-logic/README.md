# Build Logic

This directory contains the build-logic module which provides convention plugins for the Android Template project. Convention plugins help standardize build configuration across multiple modules, reducing duplication and making the build files cleaner and more maintainable.

## Available Convention Plugins

### `androidtemplate.android.application`
Configures common settings for Android application modules:
- Sets compile SDK to 36
- Sets minimum SDK to 24
- Configures Java 11 compatibility
- Applies Kotlin Android plugin

**Usage:**
```kotlin
plugins {
    id("androidtemplate.android.application")
}
```

### `androidtemplate.android.application.compose`
Configures Compose settings for Android application modules:
- Enables Compose build features
- Applies Compose Compiler plugin
- Adds Compose BOM dependencies

**Usage:**
```kotlin
plugins {
    id("androidtemplate.android.application")
    id("androidtemplate.android.application.compose")
}
```

### `androidtemplate.android.library`
Configures common settings for Android library modules:
- Sets compile SDK to 36
- Sets minimum SDK to 24
- Configures Java 11 compatibility
- Applies Kotlin Android plugin

**Usage:**
```kotlin
plugins {
    id("androidtemplate.android.library")
}
```

### `androidtemplate.android.library.compose`
Configures Compose settings for Android library modules:
- Enables Compose build features
- Applies Compose Compiler plugin
- Adds Compose BOM dependencies

**Usage:**
```kotlin
plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.library.compose")
}
```

### `androidtemplate.android.feature`
Configures settings for feature modules (combines library + compose + common dependencies):
- Applies `androidtemplate.android.library`
- Applies `androidtemplate.android.library.compose`
- Adds common feature dependencies (assumes core modules exist)

**Usage:**
```kotlin
plugins {
    id("androidtemplate.android.feature")
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
│   │   └── com/aragabz/androidtemplate/convention/
│   │       ├── AndroidCompose.kt
│   │       ├── KotlinAndroid.kt
│   │       └── ProjectExtensions.kt
│   └── build.gradle.kts
└── settings.gradle.kts
```

## Benefits

1. **DRY (Don't Repeat Yourself)**: Common configuration is centralized in one place
2. **Consistency**: All modules use the same baseline configuration
3. **Maintainability**: Updates to build configuration only need to be made once
4. **Cleaner Build Files**: Module build files focus on module-specific configuration
5. **Type Safety**: Configuration is written in Kotlin with full IDE support

## Modifying Convention Plugins

To modify the convention plugins:

1. Edit the appropriate plugin file in `build-logic/convention/src/main/kotlin/`
2. Update the shared configuration functions in the `convention` package
3. The changes will be picked up automatically by all modules using those plugins

## Adding New Convention Plugins

To add a new convention plugin:

1. Create a new plugin class in `build-logic/convention/src/main/kotlin/`
2. Register it in `build-logic/convention/build.gradle.kts` under `gradlePlugin.plugins`
3. Use the plugin in your module's build.gradle.kts with `id("androidtemplate.android.your-plugin-name")`
