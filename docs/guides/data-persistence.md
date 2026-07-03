# Data Persistence Guide

This template provides two persistence mechanisms: **Room** for structured data and **DataStore** for key-value preferences.

## Room Database

### Architecture

The project uses an **Aggregator Pattern** for Room:
- **Entities & DAOs** live in feature `:data` modules (close to the data they manage)
- **AppDatabase** lives in `:app` to aggregate all entities into a single database
- **DAOs are provided** via Hilt to feature modules

### BaseDao

All DAOs can extend `BaseDao<T>` for common CRUD operations:

```kotlin
interface BaseDao<T> {
    @Upsert
    suspend fun upsert(entity: T)

    @Upsert
    suspend fun upsertAll(entities: List<T>)

    @Delete
    suspend fun delete(entity: T)
}
```

### Example: Todo Feature

**Entity** (in `:feature:todos:data`):
```kotlin
@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Long
)
```

**DAO** (in `:feature:todos:data`):
```kotlin
@Dao
interface TodoDao : BaseDao<TodoEntity> {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getById(id: String): TodoEntity?
}
```

**Database** (in `:app`):
```kotlin
@Database(entities = [TodoEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
```

**Hilt Provider** (in `:app`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao = database.todoDao()
}
```

### Adding a New Entity

1. Define the `@Entity` in your feature's `:data` module
2. Create a `@Dao` extending `BaseDao<YourEntity>`
3. Add the entity to `@Database(entities = [...])` in `AppDatabase`
4. Add a `@Provides` function in `DatabaseModule`
5. Increment the database version or use `fallbackToDestructiveMigration()`

---

## DataStore Preferences

### UserPreferencesRepository

Interface for managing user preferences:

```kotlin
interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setLanguage(language: String)
    suspend fun setSessionToken(token: String)
    suspend fun clearSession()
}
```

### UserPreferences Model

```kotlin
data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "en",
    val sessionToken: String? = null
)

enum class AppTheme { SYSTEM, LIGHT, DARK }
```

### Usage in ViewModels

```kotlin
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val preferences = preferencesRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch { preferencesRepository.setTheme(theme) }
    }
}
```

### When to Use Which

| Use Case | Storage |
|----------|---------|
| Structured/relational data | Room |
| Lists of items | Room |
| User preferences/settings | DataStore |
| Auth tokens | DataStore |
| Simple flags (e.g., "onboarding complete") | DataStore |
