# Design System

The design system is split into two tiers for clear separation of concerns:

- **`:core:designsystem`** — Pure design tokens and basic components
- **`:core:ui`** — Complex shared screens and patterns

## Theme

### AppTheme

The main theme composable supports:
- **Material3** with full color scheme
- **Dynamic colors** on Android 12+ (falls back to brand colors)
- **Dark mode** support (System/Light/Dark via `AppTheme` enum)
- **Custom spacing** via `LocalSpacing` CompositionLocal

```kotlin
AppTheme(darkTheme = isDark, dynamicColor = true) {
    // Your content
}
```

### Design Tokens

#### Colors
- Full Material3 light and dark color schemes
- Semantic colors for status states (success, warning, error)

#### Spacing
Available via `LocalSpacing.current`:

| Token | Value |
|-------|-------|
| `extraSmall` | 4.dp |
| `small` | 8.dp |
| `medium` | 16.dp |
| `large` | 24.dp |
| `extraLarge` | 32.dp |
| `huge` | 48.dp |

```kotlin
val spacing = LocalSpacing.current
Modifier.padding(spacing.medium)
```

#### Typography
Uses Material3's default type scale. Customizable via `AppTheme`.

#### Shapes
Material3 shape system with small/medium/large corner radius definitions.

---

## Components

### AppButton

A multi-variant button supporting loading states and icons.

```kotlin
AppButton(
    text = "Submit",
    onClick = { },
    variant = AppButtonVariant.PRIMARY,
    isLoading = false,
    leadingIcon = Icons.Default.Check
)
```

| Variant | Use Case |
|---------|----------|
| `PRIMARY` | Main actions |
| `SECONDARY` | Secondary actions |
| `GHOST` | Tertiary/text-only actions |
| `DESTRUCTIVE` | Dangerous/delete actions |

### AppImage

Coil-powered image component with built-in placeholders:

```kotlin
AppImage(
    url = "https://example.com/image.jpg",
    contentDescription = "Profile photo",
    modifier = Modifier.size(120.dp)
)
```

Features:
- Default cross-fade animation
- Loading placeholder
- Error placeholder
- Enforced accessibility content descriptions

---

## Shared Screens (`:core:ui`)

### LoadingScreen
Full-screen centered loading indicator.

```kotlin
LoadingScreen()
```

### ErrorScreen
Error state with icon, message, and retry action.

```kotlin
ErrorScreen(
    message = "Something went wrong",
    onRetry = { viewModel.retry() }
)
```

### EmptyScreen
Empty state with illustration, message, and optional action.

```kotlin
EmptyScreen(
    title = "No items yet",
    description = "Add your first item to get started",
    actionText = "Add Item",
    onAction = { navController.navigate(Route.AddTodo) }
)
```

### NetworkBanner / OfflineBanner
Connectivity status banner that observes `NetworkMonitor`:

```kotlin
NetworkBanner(networkMonitor = networkMonitor)
```

Automatically shows/hides based on network state with animation.

---

## Window Size Classes

The design system integrates Material3 Window Size Classes for adaptive layouts:

```kotlin
val windowSizeClass = rememberWindowSizeClass()

when (windowSizeClass.widthSizeClass) {
    WindowWidthSizeClass.Compact -> PhoneLayout()
    WindowWidthSizeClass.Medium -> TabletLayout()
    WindowWidthSizeClass.Expanded -> DesktopLayout()
}
```

## Custom Lint Rules

The `:lint` module enforces design system usage:
- **DirectColorUsageDetector** — Forbids `Color(0xFF...)` in favor of theme colors
- **ViewModelConventionDetector** — Enforces `ViewModel` suffix naming
