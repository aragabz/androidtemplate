# Architecture Patterns

## AppResult

A sealed interface for wrapping asynchronous operation results consistently across all layers.

```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val exception: AppError) : AppResult<Nothing>
    data object Loading : AppResult<Nothing>
}
```

**Usage in repositories:**
```kotlin
fun getTodos(): Flow<AppResult<List<Todo>>>
```

**Usage in ViewModels:**
```kotlin
viewModel.uiState.collect { result ->
    when (result) {
        is AppResult.Loading -> showLoading()
        is AppResult.Success -> showData(result.data)
        is AppResult.Error -> showError(result.exception)
    }
}
```

## Use Case Pattern

Base use case classes in `:core:domain` provide structured business logic execution:

```kotlin
// Suspending use case with parameters
abstract class UseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P): AppResult<R>
}

// Flow-based use case with parameters
abstract class FlowUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    operator fun invoke(parameters: P): Flow<AppResult<R>>
}

// No-parameter variants
abstract class NoParamUseCase<out R>(dispatcher: CoroutineDispatcher)
abstract class NoParamFlowUseCase<out R>(dispatcher: CoroutineDispatcher)
```

## Repository Pattern

Repositories are defined as interfaces in the domain layer and implemented in the data layer:

```kotlin
// In :feature:todos:domain
interface TodosRepository {
    fun getTodos(): Flow<AppResult<List<Todo>>>
    suspend fun addTodo(todo: Todo): AppResult<Unit>
    suspend fun deleteTodo(id: String): AppResult<Unit>
}

// In :feature:todos:data
class TodosRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val preferences: UserPreferencesRepository
) : TodosRepository { ... }
```

## Database Aggregator Pattern

Room entities and DAOs live in feature `:data` modules, but the `AppDatabase` class lives in `:app` to aggregate all entities:

```kotlin
// In :app
@Database(entities = [TodoEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
```

DAOs are provided via Hilt from the app module to feature data modules.

## NetworkMonitor Pattern

Reactive connectivity monitoring for offline-first features:

```kotlin
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
```

Implemented with `ConnectivityManager` callbacks and exposed as a `StateFlow` for UI consumption.

## Session Manager Pattern

Global authentication state management with 401 detection:

```kotlin
interface SessionManager {
    val unauthorizedEvent: SharedFlow<Unit>
    fun onUnauthorized()
}
```

The `ApiResultCallAdapterFactory` automatically triggers `onUnauthorized()` on 401 responses, enabling global session expiry handling.

## Convention Plugin Pattern

Build configuration is centralized in `build-logic/convention/`:

```kotlin
// In feature module build.gradle.kts
plugins {
    id("androidtemplate.android.feature")  // Applies library + compose + hilt + common deps
}
```

This replaces repetitive build configuration with single-line plugin applications.
