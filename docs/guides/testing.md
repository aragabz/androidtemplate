# Testing Guide

## Test Stack

| Library | Version | Purpose |
|---------|---------|---------|
| JUnit 4 | 4.13.2 | Unit test framework |
| Turbine | 1.2.1 | Kotlin Flow testing |
| MockK | 1.14.11 | Mocking library |
| Coroutines Test | 1.11.0 | `runTest`, `TestDispatcher` |
| Roborazzi | 1.64.0 | Screenshot/snapshot testing |
| Robolectric | 4.16.1 | Android framework simulation |
| Espresso | 3.7.0 | UI instrumented tests |
| UiAutomator | 2.3.0 | Device-level UI testing |
| Macrobenchmark | 1.5.0-alpha06 | Performance benchmarking |

## Running Tests

```bash
# All unit tests
./gradlew test

# Specific module
./gradlew :feature:todos:domain:test
./gradlew :core:common:test

# Instrumented tests
./gradlew connectedAndroidTest

# Screenshot tests
./gradlew recordRoborazziDebug     # Record baselines
./gradlew verifyRoborazziDebug     # Verify against baselines

# Benchmarks
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

## Unit Test Structure

Follow the **Arrange-Act-Assert** pattern:

```kotlin
class GetTodosUseCaseTest {
    private val repository = mockk<TodosRepository>()
    private val useCase = GetTodosUseCase(repository)

    @Test
    fun `returns todos when repository succeeds`() = runTest {
        // Arrange
        val todos = listOf(Todo(id = "1", title = "Test"))
        coEvery { repository.getTodos() } returns flowOf(AppResult.Success(todos))

        // Act & Assert
        useCase().test {
            assertEquals(AppResult.Success(todos), awaitItem())
            awaitComplete()
        }
    }
}
```

## Flow Testing with Turbine

```kotlin
@Test
fun `emits loading then success`() = runTest {
    viewModel.uiState.test {
        assertEquals(UiState.Loading, awaitItem())
        assertEquals(UiState.Success(data), awaitItem())
        cancelAndIgnoreRemainingEvents()
    }
}
```

## ViewModel Testing

```kotlin
@Test
fun `loads todos on init`() = runTest {
    val viewModel = TodosViewModel(getTodosUseCase)

    viewModel.uiState.test {
        val state = awaitItem()
        assertTrue(state is TodosUiState.Success)
        assertEquals(3, (state as TodosUiState.Success).todos.size)
    }
}
```

## Screenshot Testing with Roborazzi

```kotlin
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DesignSystemScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun appButton_primary() {
        composeRule.setContent {
            AppTheme {
                AppButton(text = "Click", onClick = {}, variant = AppButtonVariant.PRIMARY)
            }
        }
        composeRule.onRoot().captureRoboImage()
    }
}
```

## Performance Testing

The `:baselineprofile` module includes:

**Baseline Profile Generation:**
```kotlin
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() {
        rule.collect(packageName = "com.aragabz.androidtemplate") {
            startActivityAndWait()
            // Navigate through critical paths
        }
    }
}
```

**Startup Benchmarks:**
```kotlin
@RunWith(AndroidJUnit4::class)
class StartupBenchmarks {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationFull() {
        rule.measureRepeated(
            packageName = "com.aragabz.androidtemplate",
            metrics = listOf(StartupTimingMetric()),
            compilationMode = CompilationMode.Full(),
            startupMode = StartupMode.COLD
        ) {
            startActivityAndWait()
        }
    }
}
```

## Test Organization

```
module/
└── src/
    ├── main/           # Production code
    ├── test/           # Unit tests (JVM)
    └── androidTest/    # Instrumented tests (device/emulator)
```
