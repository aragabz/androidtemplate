# Feature Modules

Feature modules implement self-contained business features following a three-layer structure: **domain**, **data**, and **ui**.

## Structure

Each feature module follows this pattern:

```
feature/<name>/
├── domain/                    # Pure business logic
│   ├── model/                 # Domain models
│   ├── repository/            # Repository interfaces
│   └── usecase/               # Use cases
├── data/                      # Data implementation
│   ├── local/
│   │   ├── entity/            # Room entities
│   │   └── dao/               # Room DAOs
│   ├── remote/                # API services (if needed)
│   ├── repository/            # Repository implementations
│   └── di/                    # Hilt data modules
└── ui/                        # Presentation
    └── presentation/
        ├── navigation/        # Feature navigation graph
        ├── <screen>/          # Screen + ViewModel pairs
        └── components/        # Feature-specific composables
```

---

## :feature:todos

A complete reference implementation demonstrating the full architecture.

### :feature:todos:domain

| Component | Purpose |
|-----------|---------|
| `Todo` | Domain model (id, title, description, isCompleted, createdAt) |
| `TodosRepository` | Interface: getTodos, getTodo, addTodo, updateTodo, deleteTodo, toggleComplete |
| `GetTodosUseCase` | Retrieves all todos as `Flow<AppResult<List<Todo>>>` |
| `GetTodoUseCase` | Retrieves single todo by ID |
| `AddTodoUseCase` | Creates a new todo |
| `UpdateTodoUseCase` | Updates an existing todo |
| `DeleteTodoUseCase` | Deletes a todo |
| `ToggleTodoCompleteUseCase` | Toggles completion status |

**Dependencies:** `core:common`, `core:domain`, `kotlinx-coroutines-android`, `javax.inject`

### :feature:todos:data

| Component | Purpose |
|-----------|---------|
| `TodoEntity` | Room entity mapping |
| `TodoDao` | Extends `BaseDao<TodoEntity>` with custom queries |
| `TodosRepositoryImpl` | Room + DataStore backed implementation with seed data |
| `TodosDataModule` | Hilt bindings for repository |

**Key behaviors:**
- Seeds default todos on first app launch (tracked via DataStore flag)
- All operations wrapped in `AppResult` for consistent error handling
- Uses `Flow` for reactive data observation

**Dependencies:** `feature:todos:domain`, `core:database`, `core:datastore`, `core:common`

### :feature:todos:ui

| Component | Purpose |
|-----------|---------|
| `TodosNavigation` | Feature navigation graph with routes |
| `TodosScreen` | List screen with add FAB, swipe-to-delete |
| `TodosViewModel` | State management for the list |
| `AddTodoScreen` | Form for creating new todos |
| `AddTodoViewModel` | Validation and submission logic |
| `TodoDetailsScreen` | Detail view with edit/delete actions |
| `TodoDetailsViewModel` | Single-todo state management |

**Dependencies:** `feature:todos:domain`, `feature:todos:data`, `core:common`, `core:ui`, `core:navigation`, Hilt Navigation Compose

---

## :feature:home

The app's landing and shell module.

| Component | Purpose |
|-----------|---------|
| `SplashScreen` | Animated splash with title, navigates to Main after delay |
| `SplashViewModel` | 1.5s delay then emits navigation event |
| `MainScreen` | Bottom navigation shell (Home tab, Todos tab) |
| `homeGraph` | Navigation graph wiring Splash → Main → feature screens |

**Dependencies:** `core:common`, `core:domain`, `core:network`, `core:database`, `core:datastore`, `core:navigation`, `feature:todos:ui`

---

## Creating a New Feature

Use the provided script to scaffold a new feature:

```bash
./create_feature.sh <feature_name>
```

This generates the full three-module structure with:
- Build files with correct convention plugins
- Package structure
- Boilerplate Entity, DAO, Repository interface + implementation
- ViewModel and Screen composables
- Navigation graph
- Hilt DI module

After running, register the new modules in `settings.gradle.kts`:
```kotlin
include(":feature:<name>:domain")
include(":feature:<name>:data")
include(":feature:<name>:ui")
```
