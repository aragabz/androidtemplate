# Networking Guide

The networking layer lives in `:core:network` and provides a production-ready HTTP stack.

## Stack

| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 3.0.0 | Type-safe REST client |
| OkHttp | 5.4.0 | HTTP client with interceptors |
| kotlinx.serialization | 1.11.0 | JSON serialization/deserialization |

## Configuration

All networking is configured via Hilt in `NetworkModule`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(MockInterceptor()) // Remove in production
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ApiResultCallAdapterFactory(sessionManager))
            .build()
}
```

## ApiResultCallAdapterFactory

Custom call adapter that wraps all API responses into `AppResult<T>`:

```kotlin
// Instead of:
interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): Response<List<TodoDto>>  // Must handle errors manually
}

// You write:
interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): AppResult<List<TodoDto>>  // Errors handled automatically
}
```

**Behavior:**
- 2xx → `AppResult.Success(body)`
- 401 → Triggers `SessionManager.onUnauthorized()` + `AppResult.Error`
- Other errors → `AppResult.Error(AppError.Network(...))`
- Exceptions → `AppResult.Error(AppError.Unknown(...))`

## Session Management

The `SessionManager` broadcasts authentication failures:

```kotlin
interface SessionManager {
    val unauthorizedEvent: SharedFlow<Unit>
    fun onUnauthorized()
}
```

Observe in your Activity/ViewModel to handle session expiry (e.g., redirect to login).

## Mock Interceptor

For development without a backend, `MockInterceptor` returns canned JSON responses based on the request path. Remove it for production builds.

## Adding a New API

1. Define the service interface in your feature's data layer:
```kotlin
interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): AppResult<List<TodoDto>>

    @POST("todos")
    suspend fun createTodo(@Body todo: CreateTodoRequest): AppResult<TodoDto>
}
```

2. Provide it via Hilt:
```kotlin
@Provides
fun provideTodoApi(retrofit: Retrofit): TodoApi =
    retrofit.create(TodoApi::class.java)
```

3. Use it in your repository implementation.

## Network Monitoring

`NetworkMonitor` in `:core:common` provides reactive connectivity status:

```kotlin
@Inject lateinit var networkMonitor: NetworkMonitor

networkMonitor.isOnline.collect { isOnline ->
    if (!isOnline) showOfflineBanner()
}
```
