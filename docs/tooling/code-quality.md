# Code Quality & Static Analysis

## Tools Overview

| Tool | Purpose | Command |
|------|---------|---------|
| **Ktlint** | Code formatting (Kotlin style) | `./gradlew ktlintCheck` / `ktlintFormat` |
| **Detekt** | Static analysis (complexity, smells) | `./gradlew detektAll` |
| **Android Lint** | Android-specific checks | `./gradlew lintDebug` |
| **Custom Lint Rules** | Project-specific conventions | Applied automatically |
| **Dependency Analysis** | Unused/misplaced dependencies | `./gradlew buildHealth` |
| **Module Graph Assertion** | Architecture rule enforcement | `./gradlew assertModuleGraph` |

## Ktlint

Code formatting based on Kotlin coding conventions.

**Configuration:** Applied via `org.jlleitschuh.gradle.ktlint` plugin (v14.2.0)

```bash
# Check formatting
./gradlew ktlintCheck

# Auto-fix formatting issues
./gradlew ktlintFormat
```

**Rules enforced:**
- Standard Kotlin coding conventions
- No wildcard imports
- Consistent indentation (4 spaces)
- Max line length: 120 characters

## Detekt

Static analysis for code complexity, naming, and style.

**Configuration:** `config/detekt/detekt.yml`

```bash
# Run on all modules
./gradlew detektAll
```

**Key rules:**
- `maxIssues = 10` — Build fails if more than 10 issues
- Complexity rules (long methods, large classes)
- Naming conventions enforced
- `TODO`/`FIXME`/`STOPSHIP` comments forbidden
- Style rules (magic numbers, unnecessary abstractions)

## Custom Lint Rules (`:lint` module)

Project-specific Android Lint checks:

| Rule | Purpose |
|------|---------|
| `DirectColorUsageDetector` | Forbids `Color(0xFF...)` — use theme colors instead |
| `ViewModelConventionDetector` | Enforces `ViewModel` suffix on ViewModel classes |

Custom rules are automatically applied to all modules via the lint module dependency.

## Dependency Analysis

Powered by `com.autonomousapps.dependency-analysis` plugin:

```bash
# Full health report
./gradlew buildHealth

# Module-specific advice
./gradlew :core:network:projectHealth
```

Detects:
- Unused dependencies (should be removed)
- Used-but-undeclared dependencies (should be added explicitly)
- Dependencies on wrong configuration (api vs implementation)

## Module Graph Assertion

Enforces architectural boundaries:

```bash
./gradlew assertModuleGraph
```

**Rules (from root `build.gradle.kts`):**
- Features can only depend on core modules
- App can depend on features and core
- Core modules can depend on other core modules
- Maximum dependency chain depth: 4

## Pre-Commit Workflow

Run before every commit:

```bash
# 1. Auto-format
./gradlew ktlintFormat

# 2. Static analysis
./gradlew ktlintCheck detektAll

# 3. Tests
./gradlew test

# 4. Android lint
./gradlew lintDebug

# Or all-in-one via Fastlane:
bundle exec fastlane quality
```

## CI Integration

All checks run in CI via GitHub Actions (see `.github/workflows/ci.yml`):
1. Architecture validation (`assertModuleGraph`)
2. Android Lint (`lintDebug`)
3. Detekt (`detektAll`)
4. Unit tests (`testDebugUnitTest`)
5. Build (`assembleDebug`)

SARIF reports are uploaded for GitHub Code Scanning integration.
