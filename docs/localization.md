# Localization & RTL Support Guidelines

This document outlines how to handle localization and Right-to-Left (RTL) support in the Android Template.

## 🌍 Language Switching

The app supports runtime language switching using a `ContextWrapper` solution and persisting the preference in `DataStore`.

### 1. Persistence
Language preferences are stored in `:core:datastore` within `UserPreferences`.

### 2. Context Wrapping
To apply the selected language, activities must override `attachBaseContext`. Since `attachBaseContext` is called before Hilt injection, you can use an `EntryPoint` or `runBlocking` with a manual instance (if necessary) or apply it in `onCreate` for less "deep" wrapping.

**Recommended approach using Hilt EntryPoint:**

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun attachBaseContext(newBase: Context) {
        // This is a simplified example. For real use, consider a separate LocaleHelper
        // that handles the DataStore access.
        val language = runBlocking {
             // Access your DataStore here
             "en" 
        }
        super.attachBaseContext(LocalizationContextWrapper.wrap(newBase, language))
    }
    
    // ...
}
```

Note: Modern Android (13+) also supports `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))` which doesn't require activity recreation manually and is the preferred way for newer systems.

## ↔️ RTL Support Guidelines

To ensure the UI looks correct in RTL languages (like Arabic), follow these rules:

### 1. Use Start/End instead of Left/Right
Always use `Start` and `End` for padding, margins, and alignment.
- **Compose:** `Modifier.padding(start = 16.dp)` instead of `padding(left = 16.dp)`.
- **ConstraintLayout:** `linkTo(parent.start)` instead of `linkTo(parent.left)`.

### 2. Icon Mirroring
Some icons should be mirrored in RTL (e.g., back arrows).
In Compose, you can use `Modifier.mirror()` (custom extension) or check the layout direction:

```kotlin
val layoutDirection = LocalLayoutDirection.current
val scaleX = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
Icon(
    imageVector = Icons.Default.ArrowBack,
    modifier = Modifier.scale(scaleX = scaleX, scaleY = 1f)
)
```

### 3. Text Alignment
Use `TextAlign.Start` for most text to let it align naturally based on the language. Avoid `TextAlign.Left` or `TextAlign.Right` unless explicitly required.

### 4. Testing
Test your UI in both LTR (English) and RTL (Arabic) using the "Force RTL layout direction" option in Developer Options or by switching the app language.

---

## 🛠 Adding a New Language

1. Add a new `strings.xml` file in a new configuration directory (e.g., `values-ar/strings.xml`).
2. Add the language code to the supported languages list in the settings UI.
3. Ensure all hardcoded strings are moved to `strings.xml`.
