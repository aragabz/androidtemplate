# Core Modules

## :core:common

Foundation utilities shared across the entire project.

| Component | Purpose |
|-----------|---------|
| `AppResult<T>` | Sealed result wrapper (Success/Error/Loading) |
| `AppError` | Structured error types for the app |
| `NetworkMonitor` | Interface for reactive connectivity status |
| `ConnectivityManagerNetworkMonitor` | Implementation using `ConnectivityManager` |
| `@Dispatcher` qualifiers | Hilt qualifiers for IO/Default/Main dispatchers |

**Dependencies:** `androidx.core-ktx`, `kotlinx-coroutines-android`, `timber`

---

## :core:network

HTTP networking layer built on Retrofit and OkHttp.

| Component | Purpose |
|-----------|---------|
| `NetworkModule` | Hilt module providing Retrofit, OkHttp, JSON converter |
| `ApiResultCallAdapterFactory` | Wraps API responses into `AppResult<T>` automatically |
| `SessionManager` | Emits unauthorized events on 401 responses |
| `MockInterceptor` | Returns canned responses for development/testing |
| Logging Interceptor | HTTP request/response logging via OkHttp |

**Key Features:**
- All API calls automatically return `AppResult<T>` — no manual try/catch needed
- Global 401 detection triggers session expiry flow
- Mock interceptor for offline development
- kotlinx.serialization for JSON parsing

**Dependencies:** `core:common`, `retrofit`, `okhttp`, `kotlinx-serialization-json`

---

## :core:database

Room database infrastructure and shared contracts.

| Component | Purpose |
|-----------|---------|
| `BaseDao<T>` | Generic DAO with upsert/upsertAll/getAll operations |

**Note:** The actual `AppDatabase` class lives in `:app` to aggregate entities from all feature modules (Aggregator Pattern).

**Dependencies:** `core:common`, `kotlinx-coroutines-android`, `kotlinx-serialization-json`

---

## :core:datastore

User preferences persistence using Jetpack DataStore.

| Component | Purpose |
|-----------|---------|
| `UserPreferencesRepository` | Interface for reading/writing user preferences |
| `UserPreferencesRepositoryImpl` | DataStore-backed implementation |
| `UserPreferences` | Data model (theme, language, session info) |
| `AppTheme` | Enum for theme modes (System/Light/Dark) |
| `DataStoreModule` | Hilt bindings |

**Dependencies:** `core:common`, `datastore-preferences`, `kotlinx-coroutines-android`

---

## :core:designsystem

Pure design tokens and basic reusable components.

| Component | Purpose |
|-----------|---------|
| `AppTheme` | Material3 theme with dynamic color support (Android 12+) |
| `Color.kt` | Light/Dark color palette definitions |
| `Spacing` | Design spacing scale (4dp–48dp) via `LocalSpacing` |
| `AppButton` | Button with 4 variants (Primary, Secondary, Ghost, Destructive) + loading state |
| `AppImage` | Coil-powered image component with placeholders |

**Key Features:**
- Dynamic color support with fallback brand theme
- Window Size Class integration for adaptive layouts
- `LocalSpacing` CompositionLocal for consistent spacing

**Dependencies:** Compose BOM, Material3, Material Icons Extended, Coil, Activity Compose

---

## :core:ui

Shared full-screen state widgets and complex UI patterns.

| Component | Purpose |
|-----------|---------|
| `LoadingScreen` | Full-screen loading indicator |
| `ErrorScreen` | Error state with message, icon, and retry button |
| `EmptyScreen` | Empty state with illustration and action button |
| `NetworkBanner` / `OfflineBanner` | Connectivity status banner observing `NetworkMonitor` |

**Dependencies:** `core:designsystem` (API dependency), `core:common`, Compose Animation, Lifecycle Compose, Coil

---

## :core:navigation

Type-safe navigation using kotlinx.serialization.

| Component | Purpose |
|-----------|---------|
| `Route` | Sealed interface with all app routes as `@Serializable` objects/data classes |
| `AppNavigationHost` | Main navigation host composable |
| `NavigationExtensions` | Helper functions for navigation actions |

**Supported Routes:** Splash, Main, Home, Todos, TodoDetails, AddTodo, Empty, Error

**Dependencies:** Navigation Compose, kotlinx-serialization-json, Lifecycle ViewModel

---

## :core:domain

Base classes for business logic use cases.

| Component | Purpose |
|-----------|---------|
| `UseCase<P, R>` | Suspending use case with parameters |
| `NoParamUseCase<R>` | Suspending use case without parameters |
| `FlowUseCase<P, R>` | Flow-emitting use case with parameters |
| `NoParamFlowUseCase<R>` | Flow-emitting use case without parameters |

All use cases execute on a configurable `CoroutineDispatcher` and wrap results in `AppResult<T>`.

**Dependencies:** `core:common`, `kotlinx-coroutines-android`

---

## :core:sync

Background synchronization using WorkManager.

| Component | Purpose |
|-----------|---------|
| `SyncManager` | Interface for scheduling/cancelling sync work |
| `WorkManagerSyncManager` | Implementation using WorkManager periodic + one-time requests |
| `SyncWorker` | HiltWorker for executing sync tasks |
| `SyncConstraints` | Network/battery/storage constraints configuration |

**Dependencies:** `core:common`, `core:domain`, `work-runtime-ktx`, Hilt

---

## :core:crash

Pluggable crash reporting abstraction.

| Component | Purpose |
|-----------|---------|
| `CrashReporter` | Interface for logging exceptions and non-fatal events |
| `TimberCrashReporter` | Debug implementation using Timber |

Swap `TimberCrashReporter` with Firebase Crashlytics or Sentry in production.

**Dependencies:** `timber`

---

## :core:flags

Simple feature flag framework.

| Component | Purpose |
|-----------|---------|
| `Feature` | Enum of feature flags |
| `FeatureFlagManager` | Interface for checking flag state |
| `DebugFeatureFlagManager` | Default implementation returning static values |

Replace with a remote config provider (Firebase Remote Config, LaunchDarkly) for production.

**Dependencies:** None (pure Kotlin)
