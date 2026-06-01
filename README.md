# Android Template Project

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/Gradle-9.5.1-green.svg)](https://gradle.org)
[![AGP](https://img.shields.io/badge/AGP-9.2.1-blue.svg)](https://developer.android.com/studio/releases/gradle-plugin)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-ready Android template showcasing modern Android development with multi-module architecture, clean architecture principles, Jetpack Compose, and comprehensive tooling.

## 📱 Features

- 🏗️ **Multi-Module Architecture** - 8 modules following clean architecture
- 🎨 **Two-Tier Design System** - Separates design tokens from UI components
- 🧭 **Type-Safe Navigation** - Navigation 3 with kotlinx.serialization
- 🌐 **Offline-First Ready** - NetworkMonitor and AppResult patterns
- 💉 **Hilt Dependency Injection** - Throughout all layers
- 🔄 **Reactive Patterns** - Kotlin Flow and StateFlow
- 🎭 **Material Design 3** - Modern UI with Light/Dark themes
- 🛠️ **Code Quality Tools** - Ktlint, Detekt, and Fastlane
- 🧪 **Testing Ready** - Unit testing structure with Turbine
- 🚀 **CI/CD Ready** - Fastlane lanes for automation

## 🏗️ Architecture

### Module Structure

```
androidtemplate/
├── app/                        # Main application module
│
├── core/
│   ├── common/                # Utilities, AppResult, NetworkMonitor
│   ├── network/               # Retrofit, OkHttp, API adapter
│   ├── database/              # Room database, BaseDao
│   ├── datastore/             # DataStore preferences
│   ├── designsystem/          # Design tokens (Theme, Colors, Spacing)
│   ├── ui/                    # Shared screens (Loading, Error, Empty)
│   └── navigation/            # Type-safe navigation routes
│
└── feature/                    # Feature modules (coming soon)
    └── [feature-name]/
        ├── data/              # Repositories, API, local storage
        ├── domain/            # Use cases, models
        └── presentation/      # ViewModels, Composables
```

### Dependency Graph

```
app → core:ui → core:designsystem
   → core:navigation
   → core:network → core:common
   → core:database → core:common
   → core:datastore → core:common
```

### Layer Responsibilities

- **core:designsystem** - Pure design tokens, basic components (Button, TextField)
- **core:ui** - Shared screens, complex patterns (LoadingScreen, NetworkBanner)
- **core:common** - Foundation utilities (AppResult, NetworkMonitor, Dispatchers)
- **core:network** - API layer (Retrofit, ApiResultCallAdapter, SessionManager)
- **core:database** - Persistence (Room, BaseDao, TypeConverters)
- **core:datastore** - User preferences (DataStore)
- **core:navigation** - Type-safe routing (Navigation 3)

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug | 2024.2.1 or later
- **JDK** 21
- **Gradle** 9.5.1+ (included via wrapper)
- **Ruby** 3.0+ (for Fastlane, optional)

### Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd androidtemplate
   ```

2. **Install dependencies** (optional, for Fastlane)
   ```bash
   gem install bundler
   bundle install
   ```

3. **Sync the project**
   ```bash
   ./gradlew tasks
   ```
   Or in Android Studio: **File → Sync Project with Gradle Files**

4. **Build the project**
   ```bash
   ./gradlew assembleDebug
   # or
   bundle exec fastlane build_debug
   ```

## 🎯 Build Commands

### Gradle Tasks

```bash
# Build
./gradlew assembleDebug              # Build debug APK
./gradlew assembleRelease            # Build release APK
./gradlew bundleRelease              # Build release AAB

# Testing
./gradlew test                       # Unit tests
./gradlew connectedAndroidTest       # Instrumented tests

# Code Quality
./gradlew ktlintCheck                # Check code style
./gradlew ktlintFormat               # Auto-format code
./gradlew detektAll                  # Static analysis

# Module-specific
./gradlew :app:test
./gradlew :core:common:ktlintCheck
```

### Fastlane Lanes

```bash
# Build
bundle exec fastlane build_debug     # Build debug APK
bundle exec fastlane build_release   # Build release APK
bundle exec fastlane build_bundle    # Build AAB

# Testing
bundle exec fastlane test            # Run unit tests

# Code Quality
bundle exec fastlane lint_kotlin     # Run ktlint
bundle exec fastlane format_kotlin   # Auto-format
bundle exec fastlane analyze         # Run detekt
bundle exec fastlane quality         # All quality checks

# CI/CD
bundle exec fastlane ci              # Full CI pipeline
```

## 🛠️ Tech Stack

### Core

| Technology | Version | Purpose |
|-----------|---------|---------|
| Kotlin | 2.3.21 | Programming language |
| Android Gradle Plugin | 9.2.1 | Build system |
| Jetpack Compose | 2026.05.01 | UI framework |
| Material3 | Latest | Design system |
| Hilt | 2.59.2 | Dependency injection |
| Coroutines | 1.11.0 | Async programming |

### Networking

| Technology | Version | Purpose |
|-----------|---------|---------|
| Retrofit | 3.0.0 | REST API client |
| OkHttp | 5.3.2 | HTTP client |
| Kotlinx Serialization | 1.11.0 | JSON serialization |

### Storage

| Technology | Version | Purpose |
|-----------|---------|---------|
| Room | 2.8.4 | Local database |
| DataStore | 1.2.1 | Key-value storage |

### Navigation

| Technology | Version | Purpose |
|-----------|---------|---------|
| Navigation Compose | 2.8.5 | Type-safe navigation |
| Kotlinx Serialization | 1.11.0 | Route serialization |

### Code Quality

| Technology | Version | Purpose |
|-----------|---------|---------|
| Ktlint | 12.1.2 | Code formatting |
| Detekt | 1.23.7 | Static analysis |
| Fastlane | ~2.220 | Build automation |

### Testing

| Technology | Version | Purpose |
|-----------|---------|---------|
| JUnit | 4.13.2 | Unit testing |
| Turbine | 1.2.1 | Flow testing |
| Mockk | 1.14.11 | Mocking |

## 📐 Architecture Patterns

### AppResult Pattern
```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val exception: Throwable) : AppResult<Nothing>
    data object Loading : AppResult<Nothing>
}
```
Used throughout the app for consistent error handling.

### NetworkMonitor
```kotlin
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
```
Reactive connectivity checking for offline-first features.

### BaseDao
```kotlin
interface BaseDao<T> {
    @Upsert suspend fun upsert(entity: T)
    @Upsert suspend fun upsertAll(entities: List<T>)
    fun getAll(): Flow<List<T>>
}
```
Consistent CRUD operations across all DAOs.

### Type-Safe Navigation
```kotlin
@Serializable
sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data class Details(val id: String) : Route
}

// Usage
navController.navigate(Route.Details(id = "123"))
```

## 🎨 Design System

### Theme Usage
```kotlin
AppTheme {
    val spacing = LocalSpacing.current
    
    Column(modifier = Modifier.padding(spacing.medium)) {
        AppButton(
            text = "Click Me",
            onClick = { },
            variant = AppButtonVariant.PRIMARY
        )
    }
}
```

### Available Components
- **AppButton** - 4 variants (Primary, Secondary, Ghost, Destructive)
- **LoadingScreen** - Full-screen loading state
- **ErrorScreen** - Error state with retry
- **EmptyScreen** - Empty state with action
- **NetworkBanner** - Offline indicator

### Design Tokens
- **Colors** - Light/Dark schemes + semantic colors
- **Typography** - Material3 type scale
- **Spacing** - 4dp to 48dp scale via `LocalSpacing`
- **Shapes** - Corner radius definitions

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :core:common:test
```

### Test Structure
```kotlin
class MyViewModelTest {
    @Test
    fun `when data loads successfully, state is updated`() = runTest {
        // Arrange
        val viewModel = MyViewModel(fakeRepository)
        
        // Act
        viewModel.loadData()
        
        // Assert
        viewModel.uiState.test {
            assertEquals(Loading, awaitItem())
            assertEquals(Success(data), awaitItem())
        }
    }
}
```

## 🚦 Code Quality

### Before Every Commit
```bash
# 1. Auto-format your code
./gradlew ktlintFormat

# 2. Check for issues
./gradlew ktlintCheck detektAll

# 3. Run tests
./gradlew test

# Or use one command:
bundle exec fastlane quality
```

### CI/CD Integration

#### GitHub Actions
```yaml
name: CI

on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: ruby/setup-ruby@v1
        with:
          bundler-cache: true
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - run: bundle exec fastlane ci
```

## 📝 Code Style

This project follows:
- **Kotlin Coding Conventions**
- **Material Design Guidelines**
- **Clean Architecture Principles**

### Rules
- Max line length: 120 characters
- Indent: 4 spaces
- No wildcard imports
- KDoc for all public APIs
- No `TODO`, `FIXME`, or `STOPSHIP` comments

## �� Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Run quality checks (`bundle exec fastlane quality`)
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Android team for Jetpack Compose
- Square for Retrofit and OkHttp
- JetBrains for Kotlin and Coroutines
- Community for amazing libraries

---

**Made with ❤️ and Kotlin**
