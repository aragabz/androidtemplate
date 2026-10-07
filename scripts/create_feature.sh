#!/bin/bash
set -euo pipefail

# --- Input validation ---------------------------------------------------------
usage() {
    cat <<'USAGE'
Usage: ./create_feature.sh <FeatureName>
Example: ./create_feature.sh Tasks

<FeatureName> must be a valid PascalCase class name (e.g. "Tasks", "UserProfile").
The script scaffolds a 3-module feature (domain / data / ui) under feature/<module>.
USAGE
}

if [ "$#" -ne 1 ]; then
    usage
    exit 1
fi

FEATURE_NAME=$1

# Feature name is used verbatim as a Kotlin class name and package segment,
# so it must be PascalCase (uppercase first char, letters/digits only).
if ! [[ "$FEATURE_NAME" =~ ^[A-Z][A-Za-z0-9]*$ ]]; then
    echo "Error: Feature name must be PascalCase (e.g. Tasks, UserProfile). Got: $FEATURE_NAME" >&2
    exit 1
fi

# Require the script to be run from the repo root.
if [ ! -f "settings.gradle.kts" ]; then
    echo "Error: settings.gradle.kts not found. Run this script from the repo root." >&2
    exit 1
fi

MODULE_NAME=$(echo "$FEATURE_NAME" | tr '[:upper:]' '[:lower:]')
PACKAGE_BASE="com.aragabz.androidtemplate.feature.$MODULE_NAME"
BASE_PATH="feature/$MODULE_NAME"

echo "🚀 Creating feature module: $FEATURE_NAME ($MODULE_NAME) -> $BASE_PATH"

# --- 1. Create directory structure -----------------------------------------
KOTLIN_PATH="com/aragabz/androidtemplate/feature/$MODULE_NAME"
DOMAIN_SRC="$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain"
DATA_SRC="$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data"
DATA_TEST="$BASE_PATH/data/src/test/kotlin/$KOTLIN_PATH/data"
UI_SRC="$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui"
UI_TEST="$BASE_PATH/ui/src/test/kotlin/$KOTLIN_PATH/ui"
UI_RES="$BASE_PATH/ui/src/main/res"

mkdir -p "$DOMAIN_SRC/model" "$DOMAIN_SRC/repository"
mkdir -p "$DATA_SRC/local/entity" "$DATA_SRC/local/dao" "$DATA_SRC/repository" "$DATA_SRC/di"
mkdir -p "$DATA_TEST/local/entity"
mkdir -p "$UI_SRC/presentation/navigation" "$UI_TEST/presentation"
mkdir -p "$UI_RES/values" "$UI_RES/values-es"

# --- 2. build.gradle.kts for each module ------------------------------------
# JUnit, coroutines-test, Turbine and :core:testing reach every module's unit tests through the convention plugins.
cat <<EOF > "$BASE_PATH/domain/build.gradle.kts"
plugins {
    id("androidtemplate.android.library")
}

android {
    namespace = "$PACKAGE_BASE.domain"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:domain"))

    implementation(libs.kotlinx.coroutines.android)
}
EOF

cat <<EOF > "$BASE_PATH/data/build.gradle.kts"
plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.room")
}

android {
    namespace = "$PACKAGE_BASE.data"
}

dependencies {
    implementation(project(":feature:$MODULE_NAME:domain"))
    implementation(project(":core:database"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}
EOF

cat <<EOF > "$BASE_PATH/ui/build.gradle.kts"
plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "$PACKAGE_BASE.ui"
}

dependencies {
    implementation(project(":feature:$MODULE_NAME:domain"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
EOF

# --- 3. Domain layer --------------------------------------------------------
cat <<EOF > "$DOMAIN_SRC/model/${FEATURE_NAME}Model.kt"
package $PACKAGE_BASE.domain.model

/**
 * Domain model for $FEATURE_NAME.
 * Fields below are a starting point; extend to match your business requirements.
 */
data class ${FEATURE_NAME}Model(
    val id: String,
    val title: String,
)
EOF

cat <<EOF > "$DOMAIN_SRC/repository/${FEATURE_NAME}Repository.kt"
package $PACKAGE_BASE.domain.repository

import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for $FEATURE_NAME operations.
 */
interface ${FEATURE_NAME}Repository {
    /**
     * Observe all $FEATURE_NAME records; emits again whenever they change.
     */
    fun observeAll(): Flow<List<${FEATURE_NAME}Model>>
}
EOF

# --- 4. Data layer ----------------------------------------------------------
cat <<EOF > "$DATA_SRC/local/entity/${FEATURE_NAME}Entity.kt"
package $PACKAGE_BASE.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "$MODULE_NAME")
data class ${FEATURE_NAME}Entity(
    @PrimaryKey
    val id: String,
    val title: String,
)
EOF

cat <<EOF > "$DATA_SRC/local/dao/${FEATURE_NAME}Dao.kt"
package $PACKAGE_BASE.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.aragabz.androidtemplate.core.database.dao.BaseDao
import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import kotlinx.coroutines.flow.Flow

@Dao
interface ${FEATURE_NAME}Dao : BaseDao<${FEATURE_NAME}Entity> {
    @Query("SELECT * FROM $MODULE_NAME")
    fun observeAll(): Flow<List<${FEATURE_NAME}Entity>>
}
EOF

cat <<EOF > "$DATA_SRC/repository/${FEATURE_NAME}RepositoryImpl.kt"
package $PACKAGE_BASE.data.repository

import $PACKAGE_BASE.data.local.dao.${FEATURE_NAME}Dao
import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed implementation: the DAO's stream is the single source of truth.
 */
@Singleton
class ${FEATURE_NAME}RepositoryImpl
    @Inject
    constructor(
        private val dao: ${FEATURE_NAME}Dao,
    ) : ${FEATURE_NAME}Repository {
        override fun observeAll(): Flow<List<${FEATURE_NAME}Model>> =
            dao.observeAll().map { entities -> entities.map { it.toExternalModel() } }
    }

fun ${FEATURE_NAME}Entity.toExternalModel(): ${FEATURE_NAME}Model =
    ${FEATURE_NAME}Model(
        id = id,
        title = title,
    )
EOF

cat <<EOF > "$DATA_SRC/di/${FEATURE_NAME}DataModule.kt"
package $PACKAGE_BASE.data.di

import $PACKAGE_BASE.data.repository.${FEATURE_NAME}RepositoryImpl
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ${FEATURE_NAME}DataModule {
    @Binds
    fun bind${FEATURE_NAME}Repository(impl: ${FEATURE_NAME}RepositoryImpl): ${FEATURE_NAME}Repository
}
EOF

cat <<EOF > "$DATA_TEST/local/entity/${FEATURE_NAME}EntityMappingTest.kt"
package $PACKAGE_BASE.data.local.entity

import $PACKAGE_BASE.data.repository.toExternalModel
import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import org.junit.Assert.assertEquals
import org.junit.Test

class ${FEATURE_NAME}EntityMappingTest {
    @Test
    fun \`toExternalModel maps all fields\`() {
        val entity = ${FEATURE_NAME}Entity(id = "1", title = "First")

        assertEquals(${FEATURE_NAME}Model(id = "1", title = "First"), entity.toExternalModel())
    }
}
EOF

# --- 5. UI layer ------------------------------------------------------------
cat <<EOF > "$UI_RES/values/strings.xml"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="${MODULE_NAME}_empty_message">Nothing here yet</string>
</resources>
EOF

cat <<EOF > "$UI_RES/values-es/strings.xml"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="${MODULE_NAME}_empty_message">Todavía no hay nada aquí</string>
</resources>
EOF

cat <<EOF > "$UI_SRC/presentation/${FEATURE_NAME}UiState.kt"
package $PACKAGE_BASE.ui.presentation

import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model

data class ${FEATURE_NAME}UiState(
    val isLoading: Boolean = false,
    val items: List<${FEATURE_NAME}Model> = emptyList(),
)
EOF

cat <<EOF > "$UI_SRC/presentation/${FEATURE_NAME}ViewModel.kt"
package $PACKAGE_BASE.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * UI state is derived from the repository stream: one collection, shared while the screen is shown
 * (and for 5 seconds after, so a configuration change does not restart it).
 */
@HiltViewModel
class ${FEATURE_NAME}ViewModel
    @Inject
    constructor(
        repository: ${FEATURE_NAME}Repository,
    ) : ViewModel() {
        val uiState: StateFlow<${FEATURE_NAME}UiState> =
            repository
                .observeAll()
                .map { items -> ${FEATURE_NAME}UiState(items = items) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = ${FEATURE_NAME}UiState(isLoading = true),
                )

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
EOF

cat <<EOF > "$UI_SRC/presentation/${FEATURE_NAME}Screen.kt"
package $PACKAGE_BASE.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.core.designsystem.theme.LocalSpacing
import com.aragabz.androidtemplate.core.ui.screens.EmptyScreen
import com.aragabz.androidtemplate.core.ui.screens.LoadingScreen
import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import $PACKAGE_BASE.ui.R

/**
 * Route: gets the ViewModel and collects its state. Keep it thin; the UI lives in the Content composable.
 */
@Composable
fun ${FEATURE_NAME}Screen(viewModel: ${FEATURE_NAME}ViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ${FEATURE_NAME}ScreenContent(uiState = uiState)
}

/**
 * Stateless UI: takes state and callbacks only, so it can be previewed and tested without a ViewModel.
 */
@Composable
internal fun ${FEATURE_NAME}ScreenContent(
    uiState: ${FEATURE_NAME}UiState,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    when {
        uiState.isLoading -> LoadingScreen(modifier = modifier)
        uiState.items.isEmpty() ->
            EmptyScreen(message = stringResource(id = R.string.${MODULE_NAME}_empty_message), modifier = modifier)
        else ->
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                }
            }
    }
}

@PreviewLightDark
@Composable
private fun ${FEATURE_NAME}ScreenContentPreview() {
    AppTheme {
        ${FEATURE_NAME}ScreenContent(
            uiState =
                ${FEATURE_NAME}UiState(
                    items =
                        listOf(
                            ${FEATURE_NAME}Model(id = "1", title = "First item"),
                            ${FEATURE_NAME}Model(id = "2", title = "Second item"),
                        ),
                ),
        )
    }
}
EOF

cat <<EOF > "$UI_SRC/presentation/navigation/${FEATURE_NAME}Navigation.kt"
package $PACKAGE_BASE.ui.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import $PACKAGE_BASE.ui.presentation.${FEATURE_NAME}Screen
import kotlinx.serialization.Serializable

/** $FEATURE_NAME list screen. The feature owns its routes; core knows nothing about them. */
@Serializable
data object ${FEATURE_NAME}Route

/**
 * Registers the $FEATURE_NAME screens.
 */
fun NavGraphBuilder.${MODULE_NAME}Screen() {
    composable<${FEATURE_NAME}Route> {
        ${FEATURE_NAME}Screen()
    }
}

/**
 * Navigate to the feature.
 */
fun NavController.navigateTo${FEATURE_NAME}() {
    navigate(${FEATURE_NAME}Route)
}
EOF

cat <<EOF > "$UI_TEST/presentation/${FEATURE_NAME}ViewModelTest.kt"
package $PACKAGE_BASE.ui.presentation

import app.cash.turbine.test
import com.aragabz.androidtemplate.core.testing.MainDispatcherRule
import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ${FEATURE_NAME}ViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class Fake${FEATURE_NAME}Repository : ${FEATURE_NAME}Repository {
        val items = MutableStateFlow<List<${FEATURE_NAME}Model>>(emptyList())

        override fun observeAll(): Flow<List<${FEATURE_NAME}Model>> = items
    }

    private val repository = Fake${FEATURE_NAME}Repository()
    private val viewModel = ${FEATURE_NAME}ViewModel(repository)

    @Test
    fun \`shows loading, then the stored items as they change\`() =
        runTest {
            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)
                assertEquals(${FEATURE_NAME}UiState(), awaitItem())

                val item = ${FEATURE_NAME}Model(id = "1", title = "First")
                repository.items.value = listOf(item)

                assertEquals(listOf(item), awaitItem().items)
            }
        }
}
EOF

# --- 6. Register modules in settings.gradle.kts ------------------------------
registered=0
for sub in domain data ui; do
    if ! grep -q ":feature:$MODULE_NAME:$sub" settings.gradle.kts; then
        perl -0pi -e 's/include\(":baselineprofile"\)/include(":feature:'"$MODULE_NAME"':'"$sub"'")\ninclude(":baselineprofile")/' settings.gradle.kts
        registered=1
    fi
done
if [ "$registered" -eq 1 ]; then
    echo "● Registered $MODULE_NAME modules in settings.gradle.kts"
else
    echo "● Modules already registered in settings.gradle.kts"
fi

# --- 7. Summary -------------------------------------------------------------
cat <<EOM

✅ $FEATURE_NAME feature scaffolded at: $BASE_PATH
Modules: :feature:$MODULE_NAME:domain | :feature:$MODULE_NAME:data | :feature:$MODULE_NAME:ui

Manual wiring still required (all in :app):
  1. database/AppDatabase.kt: add ${FEATURE_NAME}Entity::class to entities and declare
       abstract fun ${MODULE_NAME}Dao(): ${FEATURE_NAME}Dao
  2. database/DatabaseMigrations.kt: bump CURRENT_VERSION, add this migration and list it in ALL:
       val MIGRATION_<old>_<new> =
           object : Migration(<old>, <new>) {
               override fun migrate(database: SupportSQLiteDatabase) {
                   database.execSQL(
                       "CREATE TABLE IF NOT EXISTS \`$MODULE_NAME\` (\`id\` TEXT NOT NULL, \`title\` TEXT NOT NULL, PRIMARY KEY(\`id\`))",
                   )
               }
           }
  3. database/DatabaseModule.kt: add
       @Provides
       fun provide${FEATURE_NAME}Dao(db: AppDatabase): ${FEATURE_NAME}Dao = db.${MODULE_NAME}Dao()
  4. build.gradle.kts: add implementation(project(":feature:$MODULE_NAME:ui")) and
     implementation(project(":feature:$MODULE_NAME:data")).
  5. navigation/AppNavGraph.kt: call ${MODULE_NAME}Screen() and open it with navController.navigateTo${FEATURE_NAME}(),
     or show ${FEATURE_NAME}Screen() from a new MainTab in navigation/MainScreen.kt.

Then build once to export the new schema to app/schemas, and run
  ./gradlew :feature:$MODULE_NAME:data:testDebugUnitTest :feature:$MODULE_NAME:ui:testDebugUnitTest assembleDebug
EOM
