# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

CI (`.github/workflows/ci.yml`, JDK 21) runs these in order. Run the relevant ones before calling work done:

```bash
./gradlew assertModuleGraph      # architecture rules (see below)
./gradlew ktlintCheck            # ktlintFormat to auto-fix
./gradlew :app:lintDevDebug      # Android lint + custom checks from :lint; covers all modules (checkDependencies)
./gradlew detekt                 # config in config/detekt
./gradlew testDebugUnitTest :app:testDevDebugUnitTest
./gradlew compileDebugAndroidTestKotlin :app:compileDevDebugAndroidTestKotlin
./gradlew assembleDebug
```

- `:app` has an `environment` flavor dimension (`dev` / `staging` / `prod`), so app tasks are flavored: `:app:assembleDevDebug`, `:app:testDevDebugUnitTest`, `:app:installDevDebug`. Running an unflavored name like `testDebugUnitTest` or `lintDebug` silently skips `:app`. Library modules have no flavors (`:feature:todos:ui:testDebugUnitTest`).
- Instrumented tests (device/emulator required, not run in CI): `./gradlew connectedDebugAndroidTest :app:connectedDevDebugAndroidTest`.
- Lint fails the build on errors (`abortOnError = true`); `app/lint-baseline.xml` holds pre-existing issues.
- Single test: `./gradlew :feature:todos:ui:testDebugUnitTest --tests "com.aragabz.androidtemplate.feature.todos.ui.presentation.addtodo.AddTodoViewModelTest"`
- Coverage: `./gradlew :app:verifyDebugCoverage` (wired into `:app:check`; threshold from the `COVERAGE_MIN_LINE` Gradle property).
- Screenshot tests use Roborazzi via the `androidtemplate.android.screenshot.test` plugin (`recordRoborazziDebug` / `verifyRoborazziDebug`).
- Release signing reads `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` from Gradle properties or environment variables. Without them release builds are produced unsigned (never debug-signed); use the `benchmark` build type for a minified, installable build.
- Debug builds answer the endpoints in `core/network/src/debug/.../DebugMockRegistry.kt` with canned data from `MockInterceptor`; pass `-PmockApi=false` to hit the real backend. Each app flavor sets its own `BASE_URL`.

## Scripts

- `scripts/create_feature.sh <PascalName>`: run from the repo root. Scaffolds `feature/<name>/{domain,data,ui}`: model, repository, Room entity/DAO, a `stateIn` ViewModel, a route + stateless Content screen with a preview, navigation, en/es strings, and unit tests (mapping, ViewModel). Use it instead of creating feature modules by hand. It registers the modules in `settings.gradle.kts` itself, then prints the manual wiring steps: entity, DAO and migration in `AppDatabase`/`DatabaseMigrations`, a DAO provider, the `:app` dependencies, and the graph call in `AppNavGraph`.
- `scripts/init_project.sh <package> <feature> <ProjectName> <target_path>`: copies the template to a new location and renames the `com.aragabz.androidtemplate` package, the `todos` sample feature and the `AndroidTemplate` project name.

## Architecture

Multi-module Compose + Hilt + Room app with type-safe Navigation (kotlinx.serialization routes).

**Build logic.** All module configuration lives in convention plugins in `build-logic/convention` (ids `androidtemplate.*`, registered in `build-logic/convention/build.gradle.kts`). Module build files should only apply plugins, set `namespace`, and declare dependencies. Shared SDK, Kotlin or Compose settings go in the convention plugins, never in individual modules. Versions live in `gradle/libs.versions.toml`.

**Module layers.**
- `core:*`: shared infrastructure (network, database, datastore, sync, analytics, crash, flags, designsystem, ui, common, domain, testing). Core never references a feature: no feature routes, tables, cache keys or flags live there. `core:common` (results, errors, `UiText`, dispatchers) has no Compose; Compose helpers such as `UiText.asString()` live in `core:ui`.
- `feature:<name>:{domain,data,ui}`:
  - `domain` holds models, repository interfaces and use cases. It `api`-exposes `core:common` and `core:domain`. Use cases are optional: add one when it holds logic (combining repositories, validation, rules); otherwise a ViewModel may call the repository directly. Feature flags are declared by the feature, as an enum implementing `core:flags`' `Feature`.
  - `data` holds Room entities, DAOs, repository impls and the Hilt module.
  - `ui` holds screens, plus one ViewModel, UiState and Event per screen, and a `navigation/` file that declares the feature's own `@Serializable` routes and exposes a `NavGraphBuilder` extension. A feature navigates only to its own routes; anything that leaves the feature (e.g. after sign-in, sign-out) is a callback parameter the app fills in.
  - Each screen is a stateful route (`XScreen`: gets the ViewModel via `hiltViewModel()`, collects with `collectAsStateWithLifecycle`, runs effects, takes navigation lambdas, never a `NavController`) and a stateless `XScreenContent(uiState, onEvent, …, modifier)` with a `@PreviewLightDark` preview built from a static UiState.
  - ViewModels expose one `StateFlow<UiState>`. State derived from repository streams is built with `combine`/`flatMapLatest` + `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`; a refresh bumps a trigger flow (`flatMapLatest` replaces the subscription), and writes never re-collect, since the stream emits the change. A form with no outside source may hold a `MutableStateFlow`.
  - One-off effects (navigate away, show an error) are UI state, never `Channel`/`SharedFlow` events: the ViewModel sets a field (`isSaved`, `error`, `isSignInRequired`), the route reacts in a `LaunchedEffect` and reports consumption with an event or call (`OnDismissError`, `onSignInShown()`) when the screen stays.
- `feature:home` is a single ui-only module (the Home tab and its demo screens).
- `:app` owns the shell and top-level routing (`app/.../navigation/`): `AppNavGraph.kt` declares `MainRoute`, picks Auth vs Main and calls each feature's graph extension; `MainScreen.kt` is the bottom-nav shell that hosts the features' tab screens (a `when` over `MainTab`). `MainViewModel` exposes `MainUiState` (Loading / Ready with start destination and theme); the native splash stays up while Loading. It also handles sign-out (profile tab and 401s) by setting `isSignInRequired`; `MainActivity` then shows sign-in and calls `onSignInShown()`.

**Dependency rules** (enforced by `moduleGraphAssert` in the root `build.gradle.kts`): features depend only on `core:*`, and within a feature `ui`/`data` → `domain`. Only `:app` depends on features; a feature never depends on another feature. `maxHeight = 4`. Don't add exceptions: move shared contracts to `core:*` or let `:app` connect features through callbacks.

**Database.** The single Room `AppDatabase` lives in `:app` (`app/.../database/`) so features don't depend on each other. Each new entity/DAO must be added to its `@Database` list by hand, along with a `DatabaseMigrations.CURRENT_VERSION` bump (the `@Database` version reads it) and a migration. Migrations live next to it in `app/.../database/DatabaseMigrations.kt`; add each new one to `ALL` (the app registers all of them). `core:database` only holds the generic `BaseDao`. Only debug builds fall back to destructive migration. Schemas are exported to `app/schemas` and checked by `AppDatabaseMigrationTest`.

**App wiring.** `:app` supplies what core modules can't know: the flavor's `@BaseUrl` and the `AuthTokenProvider` backed by `SecureSessionStorage` (`app/.../di/NetworkConfigModule.kt`). `MainApplication` plants `CrashReportingTree` in release, forwarding warnings and errors to `CrashReporter`; swap `TimberCrashReporter` for a real backend there. Language changes from settings are applied with `AppCompatDelegate.setApplicationLocales` in `MainActivity`; the locale list is generated from translated resources.

**Testing.** Unit tests live in each module's `src/test`. The Android convention (`configureKotlinAndroid`) adds `testImplementation(project(":core:testing"))` to every module, which brings JUnit, coroutines-test and Turbine, so build files don't declare them. `core:testing` holds `MainDispatcherRule` (pass its `dispatcher` to use cases under test), `FakeUserPreferencesRepository` and `awaitItemMatching` for `combine`d state; feature-specific fakes stay in the feature's tests. Test `stateIn` ViewModels by collecting `uiState` with Turbine (`test { }`), since `WhileSubscribed` doesn't run without a subscriber. Compose `*Content` tests use Robolectric (the screenshot-test plugin brings it).

**Custom lint** (`:lint`): `DirectColorUsageDetector` blocks raw `Color` constants and constructors (use `MaterialTheme.colorScheme` or `LocalSemanticColors.current` inside `AppTheme`; palette values live in `core/designsystem/.../theme/Color.kt`). `ViewModelConventionDetector` requires the `ViewModel` suffix.

## Conventions (from `.github/copilot-instructions.md`)

Keep responses lean: when changing a file, show only the changed block, summarize changes in 1–2 sentences, and skip boilerplate explanations.
