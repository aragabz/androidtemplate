# Semantic Colors

Semantic colors provide consistent visual feedback for success, warning, and info states across the app. These colors are theme-aware and automatically adapt to light/dark mode.

## Available Colors

### Success Colors
- **success**: Main success color (green)
- **onSuccess**: Text/icon color on success backgrounds
- **successContainer**: Lighter success background
- **onSuccessContainer**: Text/icon color on success containers

### Warning Colors
- **warning**: Main warning color (orange)
- **onWarning**: Text/icon color on warning backgrounds
- **warningContainer**: Lighter warning background
- **onWarningContainer**: Text/icon color on warning containers

### Info Colors
- **info**: Main info color (blue)
- **onInfo**: Text/icon color on info backgrounds
- **infoContainer**: Lighter info background
- **onInfoContainer**: Text/icon color on info containers

## Usage

### Accessing Semantic Colors

```kotlin
@Composable
fun MyComponent() {
    val semanticColors = LocalSemanticColors.current
    
    Icon(
        imageVector = Icons.Default.CheckCircle,
        tint = semanticColors.success,
        contentDescription = "Success"
    )
}
```

### Success Indicator Example

```kotlin
@Composable
fun SuccessMessage(message: String) {
    val semanticColors = LocalSemanticColors.current
    
    Row(
        modifier = Modifier
            .background(semanticColors.successContainer)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            tint = semanticColors.success,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = semanticColors.onSuccessContainer
        )
    }
}
```

### Warning Banner Example

```kotlin
@Composable
fun WarningBanner(message: String) {
    val semanticColors = LocalSemanticColors.current
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = semanticColors.warningContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                tint = semanticColors.warning,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = semanticColors.onWarningContainer
            )
        }
    }
}
```

### Info Tooltip Example

```kotlin
@Composable
fun InfoTooltip(text: String) {
    val semanticColors = LocalSemanticColors.current
    
    Surface(
        color = semanticColors.infoContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                tint = semanticColors.info,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = semanticColors.onInfoContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
```

## Color Values

### Light Theme
- Success: Green (#4CAF50)
- Warning: Orange (#FF9800)
- Info: Blue (#2196F3)

### Dark Theme
- Success: Light Green (#81C784)
- Warning: Light Orange (#FFB74D)
- Info: Light Blue (#64B5F6)

All colors follow Material Design 3 color system principles with appropriate contrast ratios for accessibility.

## Best Practices

1. **Use containers for backgrounds**: `successContainer`, `warningContainer`, `infoContainer` provide better readability
2. **Pair with proper text colors**: Always use `onSuccess`, `onWarning`, `onInfo` for text on colored backgrounds
3. **Don't overuse**: Reserve semantic colors for actual feedback states, not general UI decoration
4. **Consider context**: Success in one context (data saved) may differ from another (test passed)
5. **Accessibility**: All color combinations meet WCAG AA contrast requirements

## Theme Integration

Semantic colors are automatically provided through `AppTheme` and switch between light/dark variants based on the system theme:

```kotlin
AppTheme {
    // Semantic colors automatically adapt to light/dark theme
    MyScreen()
}
```
