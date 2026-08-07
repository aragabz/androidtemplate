# Animation Utilities

Comprehensive animation utilities and helpers for consistent, reusable animations across the app.

## Location

All animation utilities are in `:core:designsystem/animation/`:

```
core/designsystem/src/main/kotlin/.../animation/
├── AnimationSpecs.kt         - Standard animation specs (duration, easing)
├── Transitions.kt             - Enter/exit transitions + composable wrappers
├── LoadingAnimations.kt       - Shimmer, pulsing, rotating animations
├── InteractiveAnimations.kt   - Press, shake, bounce, rotate modifiers
└── ContentTransitions.kt      - Animated content transitions
```

## AnimationSpecs

Standard animation timing and easing specifications.

```kotlin
import com.aragabz.androidtemplate.core.designsystem.animation.AnimationSpecs

// Use predefined specs
AnimationSpecs.fast          // 150ms - Button presses, toggles
AnimationSpecs.standard      // 300ms - Screen transitions, visibility
AnimationSpecs.slow          // 500ms - Loading, onboarding
AnimationSpecs.springMedium  // Bouncy interactive elements
AnimationSpecs.springHigh    // Quick, snappy interactions
```

## Transitions

### Pre-defined Transitions

```kotlin
import com.aragabz.androidtemplate.core.designsystem.animation.Transitions

// Fade
Transitions.fadeIn
Transitions.fadeOut

// Horizontal slides
Transitions.slideInFromLeft
Transitions.slideInFromRight
Transitions.slideOutToLeft
Transitions.slideOutToRight

// Vertical slides
Transitions.slideInFromTop
Transitions.slideInFromBottom
Transitions.slideOutToTop
Transitions.slideOutToBottom

// Expand/collapse
Transitions.expandFromTop
Transitions.expandFromCenter
Transitions.shrinkToTop
Transitions.shrinkToCenter
```

### Composable Transition Wrappers

```kotlin
// Fade in/out
AnimatedFade(visible = isVisible) {
    Text("Fades in and out")
}

// Slide from left
AnimatedSlideLeft(visible = isMenuOpen) {
    MenuContent()
}

// Slide from right
AnimatedSlideRight(visible = isDrawerOpen) {
    DrawerContent()
}

// Expand/collapse
AnimatedExpand(visible = isExpanded) {
    ExpandedContent()
}
```

## Loading Animations

### Shimmer Effect

```kotlin
Box(
    modifier = Modifier
        .size(200.dp, 100.dp)
        .shimmerEffect()
)

// Custom colors
Box(
    modifier = Modifier
        .shimmerEffect(
            colors = listOf(
                Color.Gray,
                Color.LightGray,
                Color.Gray,
            ),
            durationMillis = 1500,
        )
)
```

### Pulsing Animation

```kotlin
val alpha = rememberPulsingAlpha(
    minAlpha = 0.3f,
    maxAlpha = 1f,
    durationMillis = 1000,
)

Icon(
    imageVector = Icons.Default.Notifications,
    contentDescription = null,
    modifier = Modifier.alpha(alpha),
)
```

### Rotating Animation

```kotlin
val angle = rememberRotatingAngle(durationMillis = 1000)

Icon(
    imageVector = Icons.Default.Refresh,
    contentDescription = "Loading",
    modifier = Modifier.rotate(angle),
)
```

## Interactive Animations

### Press Animation

```kotlin
Button(
    onClick = { },
    modifier = Modifier.pressAnimation(
        pressedScale = 0.95f,
    ),
) {
    Text("Press me")
}
```

### Shake Animation

```kotlin
var showError by remember { mutableStateOf(false) }

TextField(
    value = text,
    onValueChange = { text = it },
    modifier = Modifier.shakeAnimation(
        enabled = showError,
        shakeDistance = 10f,
    ),
)

// Trigger shake
LaunchedEffect(error) {
    if (error != null) {
        showError = true
        delay(500)
        showError = false
    }
}
```

### Bounce Animation

```kotlin
var triggerBounce by remember { mutableStateOf(false) }

Icon(
    imageVector = Icons.Default.Check,
    contentDescription = "Success",
    modifier = Modifier.bounceAnimation(
        enabled = triggerBounce,
        bounceScale = 1.2f,
    ),
)
```

### Rotation Animation

```kotlin
var isExpanded by remember { mutableStateOf(false) }

Icon(
    imageVector = Icons.Default.ExpandMore,
    contentDescription = "Expand",
    modifier = Modifier.rotateAnimation(
        enabled = isExpanded,
        degrees = 180f,
    ),
)
```

### Elevation Animation

```kotlin
var isPressed by remember { mutableStateOf(false) }

val elevation = animateElevationAsState(
    pressed = isPressed,
    normalElevation = 2.dp,
    pressedElevation = 8.dp,
)

Card(
    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                isPressed = true
                tryAwaitRelease()
                isPressed = false
            },
        )
    },
) {
    // Content
}
```

## Content Transitions

### Fade Content Transition

```kotlin
var selectedTab by remember { mutableStateOf(0) }

AnimatedFadeContent(targetState = selectedTab) { tab ->
    when (tab) {
        0 -> HomeContent()
        1 -> ProfileContent()
        2 -> SettingsContent()
    }
}
```

### Slide Content Transition

```kotlin
var currentStep by remember { mutableStateOf(1) }

AnimatedSlideContent(targetState = currentStep) { step ->
    when (step) {
        1 -> Step1Content()
        2 -> Step2Content()
        3 -> Step3Content()
    }
}
```

## Usage Examples

### Loading Skeleton

```kotlin
@Composable
fun UserCardSkeleton() {
    Card {
        Row {
            // Avatar shimmer
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                // Name shimmer
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .shimmerEffect()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Email shimmer
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(14.dp)
                        .shimmerEffect()
                )
            }
        }
    }
}
```

### Animated Error State

```kotlin
@Composable
fun AnimatedErrorField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
) {
    var showError by remember { mutableStateOf(false) }
    
    LaunchedEffect(error) {
        if (error != null) {
            showError = true
            delay(500)
            showError = false
        }
    }
    
    TextField(
        value = value,
        onValueChange = onValueChange,
        isError = error != null,
        modifier = Modifier.shakeAnimation(
            enabled = showError,
        ),
    )
    
    AnimatedFade(visible = error != null) {
        Text(
            text = error ?: "",
            color = MaterialTheme.colorScheme.error,
        )
    }
}
```

### Animated Success Checkmark

```kotlin
@Composable
fun SuccessCheckmark(visible: Boolean) {
    var triggerBounce by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible) {
            delay(100)
            triggerBounce = true
            delay(600)
            triggerBounce = false
        }
    }
    
    AnimatedFade(visible = visible) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color.Green,
            modifier = Modifier
                .size(48.dp)
                .bounceAnimation(enabled = triggerBounce),
        )
    }
}
```

### Expandable Card

```kotlin
@Composable
fun ExpandableCard(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    modifier = Modifier.rotateAnimation(
                        enabled = expanded,
                        degrees = 180f,
                    ),
                )
            }
            
            AnimatedExpand(visible = expanded) {
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}
```

## Best Practices

### 1. Use Consistent Timing

```kotlin
// ✅ Good - Use predefined specs
Modifier.animateContentSize(animationSpec = AnimationSpecs.standard)

// ❌ Avoid - Arbitrary durations
Modifier.animateContentSize(animationSpec = tween(237))
```

### 2. Match Animation to Context

```kotlin
// Quick feedback - fast
Button(modifier = Modifier.pressAnimation())

// Screen transitions - standard
AnimatedSlideContent(targetState = screen)

// Emphasis - slow
LoadingIndicator(modifier = Modifier.alpha(rememberPulsingAlpha()))
```

### 3. Avoid Over-Animation

```kotlin
// ❌ Too much
Card(
    modifier = Modifier
        .pressAnimation()
        .bounceAnimation(true)
        .shakeAnimation(true)
        .shimmerEffect()
) { }

// ✅ Appropriate
Card(
    modifier = Modifier.pressAnimation()
) { }
```

### 4. Consider Performance

```kotlin
// Use LaunchedEffect for one-time animations
LaunchedEffect(success) {
    if (success) {
        triggerBounce = true
    }
}

// Avoid continuous animations on large lists
LazyColumn {
    items(users) { user ->
        // ❌ Shimmer on every item
        UserCard(modifier = Modifier.shimmerEffect())
    }
}
```

## Summary

Animation utilities provide:
- ✅ **Consistent timing** via AnimationSpecs
- ✅ **Pre-built transitions** for common patterns
- ✅ **Composable wrappers** for easy usage
- ✅ **Loading animations** (shimmer, pulse, rotate)
- ✅ **Interactive feedback** (press, shake, bounce)
- ✅ **Content transitions** (fade, slide)
- ✅ **Full explicit API** compliance
