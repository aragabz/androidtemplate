package com.aragabz.androidtemplate.core.common.util

/**
 * Flow extension utilities.
 *
 * NOTE: For debouncing and throttling, use the standard library functions:
 *
 * - **Debounce**: Use `kotlinx.coroutines.flow.debounce(timeoutMillis)`
 *   ```kotlin
 *   import kotlinx.coroutines.flow.debounce
 *
 *   searchQuery
 *       .debounce(300) // Wait 300ms after user stops typing
 *       .collect { query -> performSearch(query) }
 *   ```
 *
 * - **Sample (throttle)**: Use `kotlinx.coroutines.flow.sample(periodMillis)`
 *   ```kotlin
 *   import kotlinx.coroutines.flow.sample
 *
 *   locationUpdates
 *       .sample(1000) // Emit at most once per second
 *       .collect { location -> updateUI(location) }
 *   ```
 *
 * The standard library implementations are:
 * - Thread-safe and properly tested
 * - Use monotonic time (not affected by system clock changes)
 * - Handle cancellation correctly
 * - Free from race conditions
 */
