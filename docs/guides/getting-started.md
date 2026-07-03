# Getting Started

## Prerequisites

| Tool | Version | Required |
|------|---------|----------|
| Android Studio | Ladybug 2024.2.1+ | Yes |
| JDK | 21 | Yes |
| Gradle | 9.5.1+ (via wrapper) | Included |
| Ruby | 3.0+ | Optional (for Fastlane) |

## Initial Setup

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd androidtemplate
```

### 2. Initialize as a New Project (Optional)

To use this as a template for a new project:

```bash
./init_project.sh
```

This script will:
- Prompt for new package name (e.g., `com.company.myapp`)
- Prompt for new project name
- Rename all packages and directories
- Update build files and manifests

### 3. Install Fastlane (Optional)

```bash
gem install bundler
bundle install
```

### 4. Sync & Build

```bash
# Via command line
./gradlew assembleDebug

# Or in Android Studio
# File → Sync Project with Gradle Files
# Then Run ▶
```

## Project Configuration

### Key Files

| File | Purpose |
|------|---------|
| `gradle/libs.versions.toml` | Central dependency version catalog |
| `build-logic/convention/` | Gradle convention plugins |
| `settings.gradle.kts` | Module registration |
| `config/detekt/detekt.yml` | Detekt rules configuration |
| `fastlane/Fastfile` | Fastlane automation lanes |
| `.github/workflows/ci.yml` | GitHub Actions CI |
| `.github/dependabot.yml` | Automated dependency updates |

### Environment Variables

Copy the sample env file for Fastlane:
```bash
cp fastlane/.env.sample fastlane/.env
```

## Creating a New Feature

```bash
./create_feature.sh my_feature
```

This scaffolds:
```
feature/my_feature/
├── domain/    # Models, repository interfaces, use cases
├── data/      # Entities, DAOs, repository implementations
└── ui/        # Screens, ViewModels, navigation
```

Then register in `settings.gradle.kts`:
```kotlin
include(":feature:my_feature:domain")
include(":feature:my_feature:data")
include(":feature:my_feature:ui")
```

## Development Workflow

```bash
# 1. Write code

# 2. Auto-format
./gradlew ktlintFormat

# 3. Run quality checks
./gradlew ktlintCheck detektAll lintDebug

# 4. Run tests
./gradlew test

# 5. Build
./gradlew assembleDebug

# Or use Fastlane for the full pipeline:
bundle exec fastlane ci
```

## IDE Setup

### Recommended Plugins
- Kotlin
- Compose Multiplatform IDE Support
- Detekt IntelliJ Plugin

### Live Templates
The project includes Android Studio Live Templates for common patterns:
- Hilt ViewModel
- Compose Screen
- AppResult Flow
- Repository
