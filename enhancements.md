Here's the list organized into actionable TODOs with priorities:

# 🚀 High Priority (Core Architecture & Scalability)

## Architecture

* [x] Create standalone `:core:domain` module

    * [x] Move shared business logic into `:core:domain`
    * [x] Move common interfaces/repositories contracts
    * [x] Move base use cases
    * [x] Remove cross-feature dependencies and prevent circular references

* [ ] Implement centralized error handling

    * [ ] Create `ErrorMapper`
    * [ ] Map Network exceptions → `UiText`
    * [ ] Map Database exceptions → `UiText`
    * [ ] Map Validation exceptions → `UiText`
    * [ ] Integrate with `AppResult`

## Background Processing

* [x] Create `:core:sync` module

    * [x] Add WorkManager abstraction
    * [x] Support periodic synchronization
    * [x] Support background data refresh
    * [x] Support pre-fetching operations

## Analytics & Logging

* [ ] Create `:core:analytics` module

    * [ ] Define analytics interface
    * [ ] Create Firebase implementation
    * [ ] Support future providers (Mixpanel, Amplitude, custom)
    * [ ] Add centralized event tracking

---

# 🎨 UI & Design System

## Adaptive UI

* [x] Add Window Size Classes support
* [x] Create `rememberWindowSizeClass()` helper
* [x] Support:

    * [x] Phone layouts
    * [x] Tablet layouts
    * [x] Foldable layouts

## Theming

* [x] Add Android 12+ Dynamic Color support
* [x] Add fallback custom brand theme

## Navigation & Animations

* [x] Add Compose Shared Element Transitions
* [x] Create reusable shared transition helpers

## UI Testing

* [x] Integrate screenshot testing

    * [x] Evaluate Paparazzi (Selected Roborazzi for better Robolectric integration)
    * [x] Evaluate Roborazzi (Integrated)
* [x] Add multi-locale snapshot tests
* [x] Add screen-size snapshot tests

---

# ⚙️ Build System & Tooling

## Performance

* [x] Create `:baselineprofile` module
* [x] Generate baseline profiles
* [x] Measure startup improvements (Benchmarks created)
* [x] Measure scrolling/jank improvements (Benchmarks created)

## Static Analysis

* [x] Create custom Lint rules module

    * [x] Enforce ViewModel conventions
    * [x] Enforce architecture rules (Infrastructure added)
    * [x] Prevent direct color usage
    * [x] Add project-specific checks (Registry created)

## Architecture Governance

* [x] Add Dependency Analysis Plugin
* [x] Generate module dependency graph
* [x] Detect unused dependencies
* [x] Detect dependency violations

## CI/CD Enhancements

* [x] Enable Dependabot or Renovate (Enabled Dependabot)
* [x] Add Gradle build caching (Added to CI workflow)
* [x] Publish Detekt reports to Pull Requests (Via SARIF upload)
* [x] Publish Android Lint reports to Pull Requests (Via SARIF upload)
* [x] Add architecture validation checks to CI (Added ./gradlew assertModuleGraph)

---

# 🧹 Code Quality

## Formatting

* [ ] Add Ktlint plugin (`org.jlleitschuh.gradle.ktlint`)
* [ ] Configure formatting rules
* [ ] Integrate with CI pipeline

## API Visibility

* [ ] Enable `explicitApi()`
* [ ] Apply to all core modules
* [ ] Review public/internal API exposure

---

# 👨‍💻 Developer Experience (DX)

## Productivity

* [x] Create Android Studio Live Templates

    * [x] Hilt ViewModel template
    * [x] Compose Screen template
    * [x] AppResult Flow template
    * [x] Repository template

## Automation

* [x] Create `create_feature.sh`

    * [x] Generate feature module
    * [x] Generate package structure
    * [x] Generate `build.gradle.kts`
    * [x] Generate navigation setup
    * [x] Generate ViewModel
    * [x] Generate UI screen boilerplate

---

# 📱 Common Feature Boilerplates

## Localization

* [x] Implement RTL support guidelines (Created `docs/localization.md` and `Modifier.mirrorRtl()`)
* [x] Support runtime language switching (Integrated with `UserPreferencesRepository`)
* [x] Add ContextWrapper localization solution (Added `LocalizationContextWrapper`)
* [x] Create localization documentation (Created `docs/localization.md`)

## Image Loading

* [x] Create `AppImage` composable in `:core:designsystem`
* [x] Wrap Coil implementation
* [x] Add default cross-fade
* [x] Add default error placeholder
* [x] Add default loading placeholder
* [x] Enforce accessibility content descriptions

---

# 🌟 Nice-to-Have / World-Class Template Extras

* [x] Add Macrobenchmark module (Integrated into `:baselineprofile`)
* [x] Add Compose Metrics reporting (Added to `AndroidCompose.kt`)
* [x] Add Crash Reporting abstraction (Created `:core:crash`)
* [x] Add Feature Flags framework (Created `:core:flags`)
* [ ] Add Offline-first architecture sample
* [ ] Add Sample App showcasing all architecture patterns
* [x] Add Architecture Decision Records (ADR) documentation (Created `docs/adr`)
* [ ] Add automated dependency vulnerability scanning
* [ ] Add performance monitoring abstraction
* [ ] Add multi-module template documentation site

### Recommended implementation order

1. `:core:domain`
2. Centralized Error Handling
3. Analytics Module
4. WorkManager/Sync Module
5. Baseline Profiles
6. Ktlint
7. Adaptive Layouts
8. Dynamic Theming
9. Screenshot Testing
10. CI/CD Enhancements
11. Custom Lint Rules
12. Feature Generator Script
13. Localization
14. AppImage Component
15. Module Graph Validation

This order gives the highest return on investment for maintainability, scalability, and developer productivity.
