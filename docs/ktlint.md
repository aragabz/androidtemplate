# Ktlint Code Formatting

Ktlint is configured and integrated into the build system for consistent code formatting across all modules.

## Configuration

### Convention Plugin

Ktlint is automatically applied to all library and application modules via the `androidtemplate.ktlint` convention plugin.

**Applied in:**
- `AndroidLibraryConventionPlugin` - All library modules
- `AndroidApplicationConventionPlugin` - App module

### Rules Configuration

Ktlint rules are configured in two files:

**`.editorconfig`** - Primary configuration:
```properties
[*.{kt,kts}]
max_line_length = 120
indent_size = 4
insert_final_newline = true
ij_kotlin_allow_trailing_comma = true
ij_kotlin_allow_trailing_comma_on_call_site = true

# Disabled rules
ktlint_standard_no-wildcard-imports = disabled
ktlint_function_naming_ignore_when_annotated_with = Composable
ktlint_standard_multiline-expression-wrapping = disabled
```

**`.ktlintrc`** - Additional configuration:
- Experimental rules enabled
- Android Studio code style
- Compose function naming support

## Gradle Tasks

### Check Formatting

```bash
# Check single module
./gradlew :module:ktlintCheck

# Check all modules
./gradlew ktlintCheck
```

### Auto-Format

```bash
# Format single module
./gradlew :module:ktlintFormat

# Format all modules
./gradlew ktlintFormat
```

## CI Integration

Ktlint check runs automatically in the CI pipeline:

```yaml
- name: Ktlint Check
  run: ./gradlew ktlintCheck
```

The check runs **before** lint and detekt to catch formatting issues early.

## Pre-commit Hook (Optional)

To automatically format code before committing, add this to `.git/hooks/pre-commit`:

```bash
#!/bin/sh
./gradlew ktlintFormat --daemon
git add -u
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

## IDE Integration

### Android Studio / IntelliJ IDEA

1. Install the **Ktlint** plugin from the marketplace
2. Configure in **Settings → Tools → Ktlint**:
   - Enable "Format on save"
   - Point to project `.editorconfig`

### Manual IntelliJ Configuration

Apply ktlint code style:
```bash
./gradlew ktlintApplyToIdea
```

This updates `.idea/codeStyles` to match ktlint rules.

## Disabled Rules

### `no-wildcard-imports`
**Reason:** Common packages (kotlin.*, androidx.compose.* etc.) are clearer with wildcard imports.

### `function-naming`
**Reason:** Compose @Composable functions follow PascalCase convention, which conflicts with standard Kotlin naming.

### `multiline-expression-wrapping`
**Reason:** Improves test readability when creating multi-line data objects.

## Common Violations

### 1. Line Length > 120
```kotlin
// ❌ Bad
fun veryLongFunctionName(parameter1: String, parameter2: Int, parameter3: Boolean, parameter4: Double): String

// ✅ Good
fun veryLongFunctionName(
    parameter1: String,
    parameter2: Int,
    parameter3: Boolean,
    parameter4: Double,
): String
```

### 2. Missing Trailing Comma
```kotlin
// ❌ Bad
data class User(
    val id: String,
    val name: String
)

// ✅ Good
data class User(
    val id: String,
    val name: String,
)
```

### 3. Import Ordering
```kotlin
// ❌ Bad
import com.myapp.UserRepository
import androidx.compose.runtime.Composable
import kotlin.String

// ✅ Good (auto-fixed by ktlintFormat)
import androidx.compose.runtime.Composable
import com.myapp.UserRepository
import kotlin.String
```

## Troubleshooting

### Build fails with "KtLint found code style violations"

1. Run format to auto-fix:
   ```bash
   ./gradlew ktlintFormat
   ```

2. Check the report:
   ```bash
   cat build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt
   ```

3. For violations that can't be auto-fixed, update code manually

### Ktlint conflicts with IntelliJ formatting

1. Run `./gradlew ktlintApplyToIdea` to sync IntelliJ settings
2. Disable IntelliJ's "Reformat code" on save
3. Use ktlint's format on save instead

### Disable ktlint for specific code block

```kotlin
/* ktlint-disable */
// Code that violates ktlint rules
val x=1+2
/* ktlint-enable */
```

Or suppress specific rules:
```kotlin
// ktlint-disable standard:max-line-length
val longString = "This is a very long string that exceeds the max line length but is necessary for this specific case"
// ktlint-enable standard:max-line-length
```

## Summary

- ✅ Enforced in CI pipeline
- ✅ Automatic formatting available
- ✅ Consistent configuration via .editorconfig
- ✅ Compose-aware rules
- ✅ Applied to all modules automatically
