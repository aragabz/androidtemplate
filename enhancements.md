Here's the list organized into actionable TODOs with priorities:

# 🚀 High Priority (Core Architecture & Scalability)

## Architecture

* [ ] Create standalone `:core:domain` module

    * [ ] Move shared business logic into `:core:domain`
    * [ ] Move common interfaces/repositories contracts
    * [ ] Move base use cases
    * [ ] Remove cross-feature dependencies and prevent circular references

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

* [ ] Add Android 12+ Dynamic Color support
* [ ] Add fallback custom brand theme

## Navigation & Animations

* [ ] Add Compose Shared Element Transitions
* [ ] Create reusable shared transition helpers

## UI Testing

* [ ] Integrate screenshot testing

    * [ ] Evaluate Paparazzi
    * [ ] Evaluate Roborazzi
* [ ] Add multi-locale snapshot tests
* [ ] Add screen-size snapshot tests

---

# ⚙️ Build System & Tooling

## Performance

* [ ] Create `:baselineprofile` module
* [ ] Generate baseline profiles
* [ ] Measure startup improvements
* [ ] Measure scrolling/jank improvements

## Static Analysis

* [ ] Create custom Lint rules module

    * [ ] Enforce ViewModel conventions
    * [ ] Enforce architecture rules
    * [ ] Prevent direct color usage
    * [ ] Add project-specific checks

## Architecture Governance

* [ ] Add Dependency Analysis Plugin
* [ ] Generate module dependency graph
* [ ] Detect unused dependencies
* [ ] Detect dependency violations

## CI/CD Enhancements

* [ ] Enable Dependabot or Renovate
* [ ] Add Gradle build caching
* [ ] Publish Detekt reports to Pull Requests
* [ ] Publish Android Lint reports to Pull Requests
* [ ] Add architecture validation checks to CI

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

* [ ] Create Android Studio Live Templates

    * [ ] Hilt ViewModel template
    * [ ] Compose Screen template
    * [ ] AppResult Flow template
    * [ ] Repository template

## Automation

* [ ] Create `create_feature.sh`

    * [ ] Generate feature module
    * [ ] Generate package structure
    * [ ] Generate `build.gradle.kts`
    * [ ] Generate navigation setup
    * [ ] Generate ViewModel
    * [ ] Generate UI screen boilerplate

---

# 📱 Common Feature Boilerplates

## Localization

* [ ] Implement RTL support guidelines
* [ ] Support runtime language switching
* [ ] Add ContextWrapper localization solution
* [ ] Create localization documentation

## Image Loading

* [ ] Create `AppImage` composable in `:core:designsystem`
* [ ] Wrap Coil implementation
* [ ] Add default cross-fade
* [ ] Add default error placeholder
* [ ] Add default loading placeholder
* [ ] Enforce accessibility content descriptions

---

# 🌟 Nice-to-Have / World-Class Template Extras

* [ ] Add Macrobenchmark module
* [ ] Add Compose Metrics reporting
* [ ] Add Crash Reporting abstraction
* [ ] Add Feature Flags framework
* [ ] Add Offline-first architecture sample
* [ ] Add Sample App showcasing all architecture patterns
* [ ] Add Architecture Decision Records (ADR) documentation
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
