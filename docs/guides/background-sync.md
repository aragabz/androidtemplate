# Background Sync with WorkManager

The `:core:sync` module provides a WorkManager-based abstraction for background synchronization.

## Architecture

```
SyncManager (interface)
    └── WorkManagerSyncManager (implementation)
            └── SyncWorker (@HiltWorker)
```

## SyncManager

Interface for scheduling and managing background work:

```kotlin
interface SyncManager {
    fun schedulePeriodicSync(constraints: SyncConstraints = SyncConstraints())
    fun requestImmediateSync()
    fun cancelSync()
    fun isSyncRunning(): Flow<Boolean>
}
```

## SyncConstraints

Configuration for when sync should run:

```kotlin
data class SyncConstraints(
    val requireNetwork: Boolean = true,
    val requireCharging: Boolean = false,
    val requireBatteryNotLow: Boolean = true,
    val requireStorageNotLow: Boolean = true
)
```

## Usage

### Schedule Periodic Sync

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val syncManager: SyncManager
) : ViewModel() {
    init {
        syncManager.schedulePeriodicSync(
            SyncConstraints(requireNetwork = true)
        )
    }
}
```

### Request Immediate Sync

```kotlin
fun onRefresh() {
    syncManager.requestImmediateSync()
}
```

### Observe Sync Status

```kotlin
syncManager.isSyncRunning().collect { isRunning ->
    _uiState.update { it.copy(isSyncing = isRunning) }
}
```

## Implementing Sync Logic

Customize `SyncWorker` to perform your actual synchronization:

```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: YourRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncFromRemote()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }
}
```

## Integration with App

The `MainApplication` initializes sync on app startup:

```kotlin
@HiltAndroidApp
class MainApplication : Application() {
    @Inject lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()
        syncManager.schedulePeriodicSync()
    }
}
```
