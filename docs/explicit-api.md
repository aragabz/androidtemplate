# Explicit API Mode

Explicit API mode is enabled for all core modules to enforce clear API boundaries and prevent accidental public API exposure.

## Overview

**Explicit API mode** is a Kotlin compiler feature that requires:
- All public and protected declarations must have explicit visibility modifiers
- All public and protected declarations must have explicit return types
- Prevents accidental API exposure from internal implementation details

## Configuration

### Convention Plugin

Explicit API is enforced via the `androidtemplate.kotlin.explicit.api` convention plugin.

**Location:** `build-logic/convention/src/main/kotlin/.../ExplicitApiConventionPlugin.kt`

```kotlin
class ExplicitApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        extensions.configure<KotlinProjectExtension> {
            explicitApi() // Strict mode
        }
    }
}
```

### Applied Modules

Explicit API is enabled in all **12 core modules**:

- ✅ `:core:common` - Common utilities, result types, error handling
- ✅ `:core:network` - Network layer, API clients
- ✅ `:core:database` - Room database, DAOs
- ✅ `:core:datastore` - DataStore preferences, secure storage
- ✅ `:core:domain` - Business logic, use cases
- ✅ `:core:ui` - Common UI components
- ✅ `:core:designsystem` - Design system, theme
- ✅ `:core:navigation` - Navigation routes
- ✅ `:core:analytics` - Analytics tracking
- ✅ `:core:sync` - Background sync, WorkManager
- ✅ `:core:crash` - Crash reporting
- ✅ `:core:flags` - Feature flags

**NOT applied to:**
- `:feature:*` modules (internal feature implementation)
- `:app` module (application entry point)
- `:baselineprofile` (benchmark module)

## Usage in build.gradle.kts

Add the plugin to any module that exposes public APIs:

```kotlin
plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.kotlin.explicit.api")
}
```

## Rules Enforced

### 1. Explicit Visibility Modifiers

❌ **Forbidden (implicit public):**
```kotlin
class UserRepository { }  // Compiler error
fun getUser(): User { }   // Compiler error
```

✅ **Required (explicit visibility):**
```kotlin
public class UserRepository { }
public fun getUser(): User { }

// Or mark as internal if not part of public API
internal class UserRepositoryImpl { }
```

### 2. Explicit Return Types

❌ **Forbidden (inferred return type):**
```kotlin
public fun getUser(id: String) = repository.getUser(id) // Error
```

✅ **Required (explicit return type):**
```kotlin
public fun getUser(id: String): Flow<User> = repository.getUser(id)
```

### 3. Public vs Internal

**Public** - Exposed to feature modules:
```kotlin
public interface UserRepository {
    public fun getUser(id: String): Flow<User>
}
```

**Internal** - Hidden implementation details:
```kotlin
internal class UserRepositoryImpl(
    private val api: UserApi,
    private val database: UserDao,
) : UserRepository {
    override fun getUser(id: String): Flow<User> = flow {
        // Implementation
    }
}
```

## API Audit

See `docs/api-audit.md` for a complete audit of public APIs across all core modules.

**Summary:**
- **Total core modules:** 12
- **Public classes/interfaces:** ~36
- **Public functions:** ~37
- **Well-defined API boundaries** ✅

## Benefits

### 1. Prevents Accidental API Exposure

```kotlin
// ❌ Without explicit API: accidentally public
class InternalHelper { } // Visible to all modules

// ✅ With explicit API: must be intentional
internal class InternalHelper { } // Hidden from other modules
```

### 2. Forces API Design Thinking

Developers must consciously decide:
- Should this be public or internal?
- What is the return type contract?
- Is this part of the stable API?

### 3. Better IDE Support

IDEs can:
- Warn about breaking public API changes
- Show clear public vs internal boundaries
- Suggest visibility reductions

### 4. Easier API Evolution

- Clear separation between public API and internal implementation
- Safe to refactor internal code without breaking consumers
- Reduces accidental breaking changes

## Migration Guide

If adding explicit API to an existing module:

### Step 1: Add Plugin

```kotlin
plugins {
    id("androidtemplate.kotlin.explicit.api")
}
```

### Step 2: Build to Find Violations

```bash
./gradlew :module:compileDebugKotlin
```

Compiler will report all violations:
```
error: 'public' visibility modifier is required
error: type must be specified explicitly
```

### Step 3: Fix Violations

Add explicit visibility modifiers:

```kotlin
// Before
class User { }
fun getName() = "John"

// After
public class User { }
public fun getName(): String = "John"
```

### Step 4: Mark Internal APIs

Hide implementation details:

```kotlin
internal class UserMapper {
    internal fun toEntity(user: User): UserEntity { }
}
```

## Warning Mode (Temporary)

During migration, use warning mode instead of errors:

```kotlin
extensions.configure<KotlinProjectExtension> {
    explicitApiWarning() // Warnings instead of errors
}
```

Convert to strict mode once all violations are fixed:

```kotlin
extensions.configure<KotlinProjectExtension> {
    explicitApi() // Strict mode
}
```

## Best Practices

### 1. Default to Internal

```kotlin
// Start internal, promote to public only when needed
internal class FeatureImpl { }
```

### 2. Use Interfaces for Public APIs

```kotlin
// Public contract
public interface UserRepository {
    public fun getUsers(): Flow<List<User>>
}

// Internal implementation
internal class UserRepositoryImpl : UserRepository {
    override fun getUsers(): Flow<List<User>> = dao.observeUsers()
}
```

### 3. Keep Public API Minimal

Only expose what feature modules actually need:

```kotlin
// ❌ Too much exposure
public class DataManager {
    public fun processInternal() { } // Not needed by features
}

// ✅ Minimal surface
public interface DataManager {
    public fun getData(): Flow<Data>
}
```

### 4. Document Public APIs

```kotlin
/**
 * Repository for managing user data.
 * 
 * @see User
 */
public interface UserRepository {
    /**
     * Observes user changes from the database.
     * 
     * @return Flow of user list, updates on database changes
     */
    public fun observeUsers(): Flow<List<User>>
}
```

## Troubleshooting

### Build fails: "visibility modifier is required"

**Cause:** Missing visibility modifier

**Fix:** Add `public` or `internal`:
```kotlin
public class MyClass { }
```

### Build fails: "type must be specified explicitly"

**Cause:** Inferred return type

**Fix:** Add explicit return type:
```kotlin
public fun getUser(): User = User()
```

### "Cannot access internal class from feature module"

**Cause:** Feature trying to use internal API

**Fix:** Either:
1. Make the API public if it should be exposed
2. Move the code to the feature module if it's feature-specific

## Summary

- ✅ Enforced in all 12 core modules
- ✅ Prevents accidental API exposure
- ✅ Forces intentional API design
- ✅ Clearer public vs internal boundaries
- ✅ Easier to maintain and evolve APIs
- ✅ Better compile-time safety
- ✅ No runtime overhead (compile-time only)
