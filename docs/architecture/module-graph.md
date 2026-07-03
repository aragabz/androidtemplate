# Module Dependency Graph

## Visual Graph

```mermaid
graph LR
  subgraph :core
    :core:navigation["navigation"]
    :core:common["common"]
    :core:database["database"]
    :core:datastore["datastore"]
    :core:designsystem["designsystem"]
    :core:domain["domain"]
    :core:network["network"]
    :core:sync["sync"]
    :core:ui["ui"]
    :core:crash["crash"]
    :core:flags["flags"]
  end
  subgraph :feature
    :feature:home["home"]
    :feature:todos:domain["todos:domain"]
    :feature:todos:data["todos:data"]
    :feature:todos:ui["todos:ui"]
  end
  :app --> :core:common
  :app --> :core:network
  :app --> :core:database
  :app --> :core:datastore
  :app --> :core:sync
  :app --> :core:ui
  :app --> :core:navigation
  :app --> :feature:home
  :app --> :feature:todos:ui
  :core:database --> :core:common
  :core:network --> :core:common
  :core:datastore --> :core:common
  :core:sync --> :core:common
  :core:sync --> :core:domain
  :core:ui --> :core:designsystem
  :core:ui --> :core:common
  :core:domain --> :core:common
  :feature:home --> :core:common
  :feature:home --> :core:ui
  :feature:home --> :core:navigation
  :feature:home --> :feature:todos:ui
  :feature:todos:domain --> :core:common
  :feature:todos:domain --> :core:domain
  :feature:todos:data --> :feature:todos:domain
  :feature:todos:data --> :core:database
  :feature:todos:data --> :core:datastore
  :feature:todos:ui --> :feature:todos:domain
  :feature:todos:ui --> :feature:todos:data
  :feature:todos:ui --> :core:ui
  :feature:todos:ui --> :core:navigation
```

## Dependency Rules

Enforced via `module-graph-assertion` plugin in the root `build.gradle.kts`:

```kotlin
moduleGraphAssert {
    allowed = arrayOf(
        ":feature:.* -> :core:.*",    // Features can depend on core
        ":app -> :feature:.*",         // App can depend on features
        ":app -> :core:.*",            // App can depend on core
        ":core:.* -> :core:.*"         // Core modules can depend on each other
    )
    maxHeight = 4  // Maximum dependency chain depth
}
```

## Regenerating the Graph

```bash
./gradlew createModuleGraph
```

This updates the module graph in the README automatically.

## Validating the Graph

```bash
./gradlew assertModuleGraph
```

Run this in CI to prevent architecture violations.
