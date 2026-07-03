# Dependency Injection with Hilt

This project uses **Dagger Hilt** for dependency injection across all layers.

## Setup

Hilt is applied via the `androidtemplate.android.hilt` convention plugin:

```kotlin
plugins {
    id("androidtemplate.android.hilt")
}
```

This automatically applies the Hilt Gradle plugin, KSP, and adds required dependencies.

## Application Entry Point

```kotlin
@HiltAndroidApp
class MainApplication : Application() { ... }
```

## Activity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }
```

## ViewModels

```kotlin
@HiltViewModel
class TodosViewModel @Inject constructor(
    private val getTodosUseCase: GetTodosUseCase
) : ViewModel() { ... }
```

Access in Compose:
```kotlin
@Composable
fun TodosScreen(viewModel: TodosViewModel = hiltViewModel()) { ... }
```

## Module Bindings

### Network Module (`:core:network`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideRetrofit(...): Retrofit { ... }

    @Provides @Singleton
    fun provideOkHttpClient(...): OkHttpClient { ... }
}
```

### Database Module (`:app`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase { ... }

    @Provides
    fun provideTodoDao(db: AppDatabase): TodoDao = db.todoDao()
}
```

### Feature Data Module (`:feature:todos:data`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class TodosDataModule {
    @Binds
    abstract fun bindTodosRepository(impl: TodosRepositoryImpl): TodosRepository
}
```

### DataStore Module (`:core:datastore`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {
    @Binds
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
```

## Dispatcher Qualifiers

Defined in `:core:common`:
```kotlin
@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class Dispatcher(val appDispatcher: AppDispatcher)

enum class AppDispatcher { IO, Default, Main }
```

Usage:
```kotlin
class MyUseCase @Inject constructor(
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher
)
```

## WorkManager Integration

Workers use `@HiltWorker`:
```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) { ... }
```

## Best Practices

1. Use `@Binds` for interface→implementation mappings (more efficient than `@Provides`)
2. Use `@Singleton` scope only when truly needed (most repositories should be singletons)
3. Keep DI modules in the data layer — domain stays framework-free
4. Use `javax.inject.Inject` in domain/data layers to avoid Hilt dependency
