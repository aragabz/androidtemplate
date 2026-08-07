# String Localization Guide

## Overview

All user-facing strings in the app are externalized to string resources for localization support. The app currently supports:
- 🇬🇧 English (default)
- 🇪🇸 Spanish (es)

## String Resources Location

### Core UI Components
**Module:** `core:ui`
- English: `core/ui/src/main/res/values/strings.xml`
- Spanish: `core/ui/src/main/res/values-es/strings.xml`

Contains strings for:
- LoadingScreen default message
- ErrorScreen title and action button
- NetworkBanner offline message

### Design System Components
**Module:** `core:designsystem`
- English: `core/designsystem/src/main/res/values/strings.xml`
- Spanish: `core/designsystem/src/main/res/values-es/strings.xml`

Contains strings for:
- AppButton loading state (accessibility)

### Feature Modules
Each feature module has its own string resources:
- `feature/home/src/main/res/values/strings.xml`
- `feature/auth/ui/src/main/res/values/strings.xml`
- `feature/profile/ui/src/main/res/values/strings.xml`
- `feature/settings/ui/src/main/res/values/strings.xml`

## Usage Examples

### In Composable Functions

```kotlin
import androidx.compose.ui.res.stringResource
import com.aragabz.androidtemplate.core.ui.R

@Composable
fun MyScreen() {
    // Simple string
    Text(text = stringResource(R.string.loading_default_message))
    
    // String with parameters
    Text(text = stringResource(R.string.home_component_example_template, "Button"))
}
```

### Default Parameter Values

```kotlin
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading_default_message),
) {
    // Component implementation
}
```

This allows callers to either:
1. Use the default localized string
2. Provide a custom message

### Accessibility State Descriptions

```kotlin
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
) {
    val loadingStateDescription = stringResource(R.string.button_loading_state)
    val accessibilityModifier = modifier.semantics {
        if (isLoading) {
            stateDescription = loadingStateDescription
        }
    }
    // ...
}
```

## Localized Strings Reference

### Core UI Strings

| String ID | English | Spanish | Usage |
|-----------|---------|---------|-------|
| `loading_default_message` | Loading… | Cargando… | LoadingScreen default |
| `error_icon_content_description` | Error | Error | ErrorScreen icon |
| `error_title` | Something went wrong | Algo salió mal | ErrorScreen title |
| `error_action_retry` | Try Again | Intentar de nuevo | ErrorScreen button |
| `network_offline_label` | Offline | Sin conexion | NetworkBanner icon |
| `network_offline_message` | You're offline | No tienes conexion | NetworkBanner text |

### Design System Strings

| String ID | English | Spanish | Usage |
|-----------|---------|---------|-------|
| `button_loading_state` | Loading | Cargando | AppButton accessibility |

## Adding New Strings

### 1. Add to English Resources

```xml
<!-- core/ui/src/main/res/values/strings.xml -->
<resources>
    <string name="my_new_string">Hello World</string>
    
    <!-- String with parameters -->
    <string name="greeting_template">Hello %1$s!</string>
</resources>
```

### 2. Add to Spanish Resources

```xml
<!-- core/ui/src/main/res/values-es/strings.xml -->
<resources>
    <string name="my_new_string">Hola Mundo</string>
    
    <!-- String with parameters -->
    <string name="greeting_template">¡Hola %1$s!</string>
</resources>
```

### 3. Use in Code

```kotlin
@Composable
fun MyComponent() {
    // Simple string
    Text(text = stringResource(R.string.my_new_string))
    
    // String with parameters
    Text(text = stringResource(R.string.greeting_template, userName))
}
```

## Best Practices

### ✅ Do

- **Always externalize user-facing strings** to resources
- **Use semantic naming** (e.g., `error_title` not `something_went_wrong`)
- **Add comments** to group related strings in XML
- **Use parameters** for dynamic content: `"Welcome %1$s"` instead of concatenation
- **Provide translations** for all supported locales
- **Use ellipsis character** (`…`) for loading states, not three dots (`...`)

### ❌ Don't

- **Don't hardcode** user-facing strings in Kotlin code
- **Don't concatenate** strings: use parameters instead
- **Don't reuse** strings across unrelated contexts (even if text is the same)
- **Don't forget** to update all locale files when adding new strings

## Special Characters

When adding strings with special characters:

```xml
<!-- Apostrophes must be escaped -->
<string name="example">You\'re offline</string>

<!-- Quotes need escaping -->
<string name="example">He said \"Hello\"</string>

<!-- HTML entities work -->
<string name="example">Price: &lt; $100</string>

<!-- Use CDATA for complex strings -->
<string name="example"><![CDATA[<b>Bold</b> text]]></string>
```

## Testing Localization

### Manual Testing

1. **Change device language:**
   - Settings → System → Languages & input → Languages
   - Add Spanish and move to top

2. **Force restart the app** to see changes

3. **Verify all screens** display translated strings

### Automated Testing

Unit tests automatically use default locale (English). To test Spanish:

```kotlin
@Test
fun testSpanishStrings() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale("es"))
    val localizedContext = context.createConfigurationContext(config)
    
    val message = localizedContext.getString(R.string.loading_default_message)
    assertEquals("Cargando…", message)
}
```

## Future Localization

To add support for more languages:

1. Create new values directory: `values-<language-code>/`
   - French: `values-fr/`
   - German: `values-de/`
   - etc.

2. Copy `strings.xml` from `values/`

3. Translate all strings in the new file

4. Android automatically selects the correct locale at runtime

## Architecture Benefits

### 1. Centralized Management
All strings in one place per module, easy to audit and update.

### 2. Type Safety
Compile-time checking of string resource IDs via generated `R` class.

### 3. Runtime Locale Switching
Android handles locale selection automatically based on device settings.

### 4. Reusability
String resources can be referenced from:
- Kotlin/Java code
- XML layouts
- Other string resources (e.g., `@string/app_name`)

### 5. Accessibility
Localized strings work seamlessly with accessibility services.

## Related Documentation

- [Android String Resources](https://developer.android.com/guide/topics/resources/string-resource)
- [Android Localization](https://developer.android.com/guide/topics/resources/localization)
- [Material Design Internationalization](https://m3.material.io/foundations/content-design/internationalization)
