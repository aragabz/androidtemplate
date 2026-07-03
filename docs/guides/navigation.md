# Navigation Guide

This project uses **type-safe navigation** with kotlinx.serialization and Jetpack Navigation Compose.

## Route Definition

All routes are defined as `@Serializable` classes/objects in `:core:navigation`:

```kotlin
@Serializable
sealed interface Route {
    @Serializable data object Splash : Route
    @Serializable data object Main : Route
    @Serializable data object Home : Route
    @Serializable data object Todos : Route
    @Serializable data class TodoDetails(val todoId: String) : Route
    @Serializable data object AddTodo : Route
    @Serializable data object Empty : Route
    @Serializable data class Error(val message: String) : Route
}
```

## Navigation Host

The `AppNavGraph` in `:app` sets up the root navigation:

```kotlin
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Route.Splash) {
        homeGraph(navController)
        todosGraph(navController)
    }
}
```

## Feature Navigation Graphs

Each feature defines its own navigation graph extension:

```kotlin
// In :feature:todos:ui
fun NavGraphBuilder.todosGraph(navController: NavHostController) {
    composable<Route.Todos> {
        TodosScreen(
            onTodoClick = { navController.navigate(Route.TodoDetails(it)) },
            onAddClick = { navController.navigate(Route.AddTodo) }
        )
    }
    composable<Route.TodoDetails> { backStackEntry ->
        val route = backStackEntry.toRoute<Route.TodoDetails>()
        TodoDetailsScreen(todoId = route.todoId)
    }
    composable<Route.AddTodo> {
        AddTodoScreen(onBack = { navController.popBackStack() })
    }
}
```

## Navigation Extensions

Helper functions in `:core:navigation`:

```kotlin
// Navigate and clear back stack
fun NavController.navigateAndClear(route: Route) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
    }
}

// Navigate with single top
fun NavController.navigateSingleTop(route: Route) {
    navigate(route) {
        launchSingleTop = true
    }
}
```

## Bottom Navigation

The `MainScreen` in `:feature:home` implements bottom navigation:

```kotlin
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Route.Home,
                    onClick = { navController.navigateSingleTop(Route.Home) },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == Route.Todos,
                    onClick = { navController.navigateSingleTop(Route.Todos) },
                    icon = { Icon(Icons.Default.CheckCircle, "Todos") },
                    label = { Text("Todos") }
                )
            }
        }
    ) { ... }
}
```

## Adding Navigation for a New Feature

1. Define routes in `:core:navigation` `Route` sealed interface
2. Create a `<Feature>Navigation.kt` in your feature's `ui/presentation/navigation/`
3. Register the graph in `AppNavGraph`
4. Navigate using `navController.navigate(Route.YourRoute)`
