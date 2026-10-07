pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm") version "2.3.21"
    }
}
/* plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
} */
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidTemplate"
include(":app")
include(":core:common")
include(":core:analytics")
include(":core:domain")
include(":core:sync")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:ui")
include(":core:designsystem")
include(":lint")
include(":core:crash")
include(":core:flags")
include(":core:testing")
include(":feature:todos:domain")
include(":feature:todos:data")
include(":feature:todos:ui")
include(":feature:home")
include(":feature:auth:domain")
include(":feature:auth:data")
include(":feature:auth:ui")
include(":feature:profile:domain")
include(":feature:profile:data")
include(":feature:profile:ui")
include(":feature:settings:domain")
include(":feature:settings:data")
include(":feature:settings:ui")
include(":baselineprofile")

