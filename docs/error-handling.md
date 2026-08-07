# Centralized Error Handling

This document explains the centralized error handling system implemented in `:core:common`.

## Overview

The error handling system provides:
- **Consistent error mapping** from exceptions to user-friendly messages
- **Integration with AppResult** for streamlined error flow
- **Structured error records** for diagnostics and analytics
- **Type-safe error categories** with retry logic

## Components

### 1. AppError (Sealed Class)

Normalized error types representing all possible failures:

```kotlin
sealed class AppError {
    data class HttpError(val code: Int, val message: String)
    data class NetworkError(val cause: Throwable)
    data class DatabaseError(val cause: Throwable, val operation: String?)
    data class ValidationError(val message: String, val field: String?)
    data class AuthError(val message: String, val reason: AuthErrorReason)
    data class TimeoutError(val cause: Throwable?)
    data class ParsingError(val cause: Throwable)
    data class UnknownError(val cause: Throwable)
}
```

Each error includes:
- `category: ErrorCategory` - High-level categorization
- `severity: ErrorSeverity` - INFO, WARNING, ERROR, CRITICAL
- `isRetryable: Boolean` - Whether the operation should be retried

### 2. ErrorMapper

Extension functions to convert exceptions → AppError → UiText:

```kotlin
// Exception → AppError
fun Throwable.toAppError(): AppError

// Exception → UiText (for UI display)
fun Throwable.toUiText(): UiText

// Exception → ErrorRecord (for diagnostics)
fun Throwable.toErrorRecord(): ErrorRecord
```

**Automatic mapping rules:**
- `SocketTimeoutException` → `TimeoutError`
- `IOException` → `NetworkError`
- `SQLiteException` → `DatabaseError`
- `SerializationException` → `ParsingError`
- `IllegalArgumentException` → `ValidationError`

### 3. ErrorHandler Utilities

Helper functions for repositories and ViewModels:

```kotlin
// Wrap suspend functions with error handling (optional dispatcher)
suspend fun <T> withErrorHandling(
    dispatcher: CoroutineDispatcher? = null,
    block: suspend () -> T
): AppResult<T>

// Wrap Flows with error handling
fun <T> Flow<T>.catchAsAppError(): Flow<AppResult<T>>

// Convert error results to UiText for display
fun <T> Flow<AppResult<T>>.errorAsUiText(): Flow<UiText?>
```

## Usage Examples

### Repository Pattern

```kotlin
class UserRepository @Inject constructor(
    private val api: UserApi,
    private val database: UserDao,
) {
    // Simple case (uses current context)
    suspend fun getUser(id: String): AppResult<User> = withErrorHandling {
        val user = api.getUser(id)
        database.insertUser(user)
        user
    }
    
    // With explicit dispatcher (e.g., for API calls)
    suspend fun fetchUser(id: String): AppResult<User> = withErrorHandling(Dispatchers.IO) {
        api.getUser(id)
    }
    
    // Observable data with error handling
    fun observeUsers(): Flow<AppResult<List<User>>> = 
        database.observeUsers().catchAsAppError()
    
    // Custom validation
    suspend fun updateUser(user: User): AppResult<User> {
        if (user.name.isBlank()) {
            return AppResult.Error(validationError("Name required", "name"))
        }
        return withErrorHandling {
            database.updateUser(user)
            user
        }
    }
}
```

### ViewModel Pattern

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AppResult<User>>(AppResult.Loading)
    val uiState: StateFlow<AppResult<User>> = _uiState.asStateFlow()
    
    // Automatically extract error messages
    val errorMessage: StateFlow<UiText?> = uiState
        .map { it.errorUiText }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    fun loadUser(id: String) {
        viewModelScope.launch {
            _uiState.value = repository.getUser(id)
        }
    }
}
```

### Composable UI Pattern

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is AppResult.Loading -> LoadingScreen()
        is AppResult.Success -> UserContent(state.data)
        is AppResult.Error -> ErrorScreen(
            message = state.exception.toUiText().asString(),
            onRetry = { viewModel.loadUser("123") }
        )
    }
}
```

### Exhaustive Pattern Matching with fold()

Use `fold()` for type-safe exhaustive pattern matching that forces handling all three states:

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    // fold() ensures all cases are handled at compile time
    val content = uiState.fold(
        onSuccess = { user -> "Welcome ${user.name}" },
        onError = { error -> "Error: ${error.message}" },
        onLoading = { "Loading..." }
    )
    
    Text(content)
}
```

**Common fold() patterns:**

```kotlin
// Map to UI state
data class UiState(val message: String, val isLoading: Boolean)

val uiState: UiState = result.fold(
    onSuccess = { data -> UiState("Data: $data", false) },
    onError = { error -> UiState("Error: ${error.message}", false) },
    onLoading = { UiState("Loading...", true) }
)

// Nullable conversion with default
val data: User? = result.fold(
    onSuccess = { it },
    onError = { null },
    onLoading = { null }
)

// Logging with side effects
result.fold(
    onSuccess = { log("Success: $it") },
    onError = { log("Error: ${it.message}") },
    onLoading = { log("Loading") }
)
```

### Snackbar Error Display

```kotlin
@Composable
fun UserScreenWithSnackbar(viewModel: UserViewModel = hiltViewModel()) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error.asString(),
                duration = SnackbarDuration.Short
            )
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // Content
    }
}
```

## Best Practices

### 1. Always Use AppResult in Repositories

❌ **Don't:**
```kotlin
suspend fun getUser(id: String): User {
    return api.getUser(id) // Throws exception
}
```

✅ **Do:**
```kotlin
suspend fun getUser(id: String): AppResult<User> = withErrorHandling {
    api.getUser(id)
}
```

### 2. Use Specific Error Types

❌ **Don't:**
```kotlin
throw Exception("User not found")
```

✅ **Do:**
```kotlin
return AppResult.Error(validationError("User not found", "userId"))
```

### 3. Provide Context in Errors

❌ **Don't:**
```kotlin
AppError.DatabaseError(e)
```

✅ **Do:**
```kotlin
AppError.DatabaseError(e, operation = "insert")
```

## Summary

The centralized error handling system provides:
- ✅ **Type-safe** error handling with sealed classes
- ✅ **Consistent** error messages across the app
- ✅ **Localized** error strings via UiText
- ✅ **Structured** error records for diagnostics
- ✅ **Retryable** error classification
- ✅ **Integration** with AppResult flow
- ✅ **Testable** error mapping logic
