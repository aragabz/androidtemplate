# AndroidTemplate

A plug & play, multi-module Android starter: Jetpack Compose, Hilt, Room, type-safe Navigation
(kotlinx.serialization routes), Retrofit/OkHttp, DataStore and WorkManager, with convention plugins,
architecture rules, static analysis, screenshot tests and CI already wired up. It ships with sample
features (`todos`, `auth`, `profile`, `settings`, `home`) that show the intended layering.

## Quick start

Requirements: JDK 21 (Gradle provisions it for the daemon via `gradle/gradle-daemon-jvm.properties`) and the Android SDK.

<!-- template-only: init_project.sh removes this block from generated projects -->
Create a new project from the template (run from this repo; the target must be outside it):

```bash
./scripts/init_project.sh com.example.myapp tasks MyApp ~/AndroidProjects/MyApp
```

This copies the template, renames the `com.aragabz.androidtemplate` package, the `todos` sample feature
(`Todo*` classes become `Task*`) and the `AndroidTemplate` project name. An optional fifth argument sets
the singular feature name when stripping a trailing `s` is wrong.

<!-- /template-only -->
Add a feature (from the project root):

```bash
./scripts/create_feature.sh Tasks
```

It scaffolds `feature/tasks/{domain,data,ui}` (the domain module is pure Kotlin/JVM), registers the modules
in `settings.gradle.kts` and prints the remaining manual wiring (database entity/DAO, `:app` dependencies,
navigation graph).

## Common commands

```bash
./gradlew :app:assembleDevDebug          # :app has dev/staging/prod flavors
./gradlew assertModuleGraph ktlintCheck detekt :app:lintDevDebug
./gradlew testDebugUnitTest :app:testDevDebugUnitTest :lint:test
./gradlew :app:verifyDebugCoverage       # COVERAGE_MIN_LINE in gradle.properties
./gradlew :core:ui:verifyRoborazziDebug  # screenshot tests (recordRoborazziDebug to update)
./gradlew :app:assembleProdRelease       # R8; signed only when RELEASE_* properties are set
./gradlew buildHealth                    # dependency-analysis report
./gradlew createModuleGraph              # regenerates the graph below
```

`CLAUDE.md` documents the full CI sequence, the module layers and dependency rules, the database setup
and the Gradle properties (`VERSION_CODE`, `VERSION_NAME`, `COVERAGE_MIN_LINE`, `composeCompilerReports`,
release signing). `build-logic/README.md` describes the convention plugins.

## Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:common["common"]
    :core:domain["domain"]
    :core:database["database"]
    :core:sync["sync"]
    :core:datastore["datastore"]
    :core:ui["ui"]
    :core:analytics["analytics"]
    :core:network["network"]
    :core:crash["crash"]
    :core:flags["flags"]
    :core:designsystem["designsystem"]
  end
  subgraph :feature
    :feature:home["home"]
  end
  subgraph :feature:auth
    :feature:auth:ui["ui"]
    :feature:auth:domain["domain"]
    :feature:auth:data["data"]
  end
  subgraph :feature:profile
    :feature:profile:ui["ui"]
    :feature:profile:data["data"]
    :feature:profile:domain["domain"]
  end
  subgraph :feature:settings
    :feature:settings:ui["ui"]
    :feature:settings:data["data"]
    :feature:settings:domain["domain"]
  end
  subgraph :feature:todos
    :feature:todos:domain["domain"]
    :feature:todos:ui["ui"]
    :feature:todos:data["data"]
  end
  :feature:todos:domain --> :core:common
  :feature:todos:domain --> :core:domain
  :core:database --> :core:common
  :core:sync --> :core:common
  :core:sync --> :core:domain
  :core:sync --> :core:datastore
  :feature:todos:ui --> :core:ui
  :feature:todos:ui --> :feature:todos:domain
  :feature:todos:ui --> :core:common
  :app --> :core:common
  :app --> :core:analytics
  :app --> :core:network
  :app --> :core:database
  :app --> :core:datastore
  :app --> :core:sync
  :app --> :core:crash
  :app --> :core:flags
  :app --> :core:ui
  :app --> :feature:home
  :app --> :feature:todos:ui
  :app --> :feature:todos:data
  :app --> :feature:auth:ui
  :app --> :feature:auth:domain
  :app --> :feature:auth:data
  :app --> :feature:profile:ui
  :app --> :feature:profile:data
  :app --> :feature:settings:ui
  :app --> :feature:settings:data
  :feature:home --> :core:ui
  :feature:settings:domain --> :core:common
  :feature:settings:domain --> :core:domain
  :core:ui --> :core:designsystem
  :core:ui --> :core:common
  :core:domain --> :core:common
  :feature:auth:data --> :feature:auth:domain
  :feature:auth:data --> :core:database
  :feature:auth:data --> :core:datastore
  :feature:auth:data --> :core:common
  :feature:auth:data --> :core:network
  :feature:auth:domain --> :core:common
  :feature:auth:domain --> :core:domain
  :feature:todos:data --> :feature:todos:domain
  :feature:todos:data --> :core:database
  :feature:todos:data --> :core:datastore
  :feature:todos:data --> :core:common
  :feature:todos:data --> :core:sync
  :feature:profile:domain --> :core:common
  :feature:profile:domain --> :core:domain
  :core:crash --> :core:common
  :feature:profile:data --> :feature:profile:domain
  :feature:profile:data --> :core:database
  :feature:profile:data --> :core:datastore
  :feature:profile:data --> :core:common
  :feature:profile:data --> :core:network
  :feature:profile:ui --> :core:ui
  :feature:profile:ui --> :feature:profile:domain
  :feature:profile:ui --> :core:common
  :feature:settings:ui --> :core:ui
  :feature:settings:ui --> :feature:settings:domain
  :feature:settings:ui --> :core:common
  :feature:auth:ui --> :core:ui
  :feature:auth:ui --> :feature:auth:domain
  :feature:auth:ui --> :core:common
  :core:network --> :core:common
  :feature:settings:data --> :feature:settings:domain
  :feature:settings:data --> :core:database
  :feature:settings:data --> :core:datastore
  :feature:settings:data --> :core:common
  :core:analytics --> :core:common
  :core:datastore --> :core:common
```