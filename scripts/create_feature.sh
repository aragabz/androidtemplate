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

mkdir -p "$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain/model"
mkdir -p "$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain/repository"
mkdir -p "$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain/usecase"

mkdir -p "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/local/entity"
mkdir -p "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/local/dao"
mkdir -p "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/repository"
mkdir -p "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/di"
mkdir -p "$BASE_PATH/data/src/main/resources/META-INF/services"

mkdir -p "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation"
mkdir -p "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation/navigation"

# --- 2. build.gradle.kts for each module ------------------------------------
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
    implementation(libs.javax.inject)
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
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}
EOF

cat <<EOF > "$BASE_PATH/ui/build.gradle.kts"
plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "$PACKAGE_BASE.ui"
}

dependencies {
    implementation(project(":feature:$MODULE_NAME:domain"))
    implementation(project(":feature:$MODULE_NAME:data"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
EOF

# --- 3. Domain layer --------------------------------------------------------
cat <<EOF > "$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain/model/${FEATURE_NAME}Model.kt"
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

cat <<EOF > "$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain/repository/${FEATURE_NAME}Repository.kt"
package $PACKAGE_BASE.domain.repository

import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for $FEATURE_NAME operations.
 */
interface ${FEATURE_NAME}Repository {
    /**
     * Observe all $FEATURE_NAME records.
     */
    fun observeAll(): Flow<List<${FEATURE_NAME}Model>>
}
EOF

cat <<EOF > "$BASE_PATH/domain/src/main/kotlin/$KOTLIN_PATH/domain/usecase/Get${FEATURE_NAME}ListUseCase.kt"
package $PACKAGE_BASE.domain.usecase

import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case that exposes the stream of $FEATURE_NAME records.
 */
class Get${FEATURE_NAME}ListUseCase
    @Inject
    constructor(
        private val repository: ${FEATURE_NAME}Repository,
    ) {
        operator fun invoke(): Flow<List<${FEATURE_NAME}Model>> = repository.observeAll()
    }
EOF

# --- 4. Data layer ----------------------------------------------------------
cat <<EOF > "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/local/entity/${FEATURE_NAME}Entity.kt"
package $PACKAGE_BASE.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "${MODULE_NAME}s")
data class ${FEATURE_NAME}Entity(
    @PrimaryKey
    val id: String,
    val title: String,
)
EOF

cat <<EOF > "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/local/dao/${FEATURE_NAME}Dao.kt"
package $PACKAGE_BASE.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import com.aragabz.androidtemplate.core.database.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ${FEATURE_NAME}Dao : BaseDao<${FEATURE_NAME}Entity> {
    @Query("SELECT * FROM ${MODULE_NAME}s")
    fun observeAll(): Flow<List<${FEATURE_NAME}Entity>>

    @Query("SELECT * FROM ${MODULE_NAME}s WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ${FEATURE_NAME}Entity?
}
EOF

cat <<EOF > "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/local/${FEATURE_NAME}EntityContributor.kt"
package $PACKAGE_BASE.data.local

import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import com.aragabz.androidtemplate.core.common.util.EntityContributor
import kotlin.reflect.KClass

class ${FEATURE_NAME}EntityContributor : EntityContributor {
    override fun getEntities(): List<KClass<*>> = listOf(${FEATURE_NAME}Entity::class)
}
EOF

echo "$PACKAGE_BASE.data.local.${FEATURE_NAME}EntityContributor" > \
    "$BASE_PATH/data/src/main/resources/META-INF/services/com.aragabz.androidtemplate.core.database.EntityContributor"

cat <<EOF > "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/repository/${FEATURE_NAME}RepositoryImpl.kt"
package $PACKAGE_BASE.data.repository

import $PACKAGE_BASE.data.local.dao.${FEATURE_NAME}Dao
import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation backed by Room.
 */
@Singleton
class ${FEATURE_NAME}RepositoryImpl
    @Inject
    constructor(
        private val dao: ${FEATURE_NAME}Dao,
    ) : ${FEATURE_NAME}Repository {
        override fun observeAll(): Flow<List<${FEATURE_NAME}Model>> = dao.observeAll().map { entities -> entities.map { it.toExternalModel() } }
    }

fun ${FEATURE_NAME}Entity.toExternalModel(): ${FEATURE_NAME}Model =
    ${FEATURE_NAME}Model(
        id = id,
        title = title,
    )
EOF

cat <<EOF > "$BASE_PATH/data/src/main/kotlin/$KOTLIN_PATH/data/di/${FEATURE_NAME}DataModule.kt"
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

# --- 5. UI layer ------------------------------------------------------------
cat <<EOF > "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation/${FEATURE_NAME}UiState.kt"
package $PACKAGE_BASE.ui.presentation

import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model

data class ${FEATURE_NAME}UiState(
    val isLoading: Boolean = false,
    val items: List<${FEATURE_NAME}Model> = emptyList(),
    val errorMessage: String? = null,
)
EOF

cat > "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation/${FEATURE_NAME}Event.kt" <<EOF
package $PACKAGE_BASE.ui.presentation

sealed interface ${FEATURE_NAME}Event {
    data object OnRefresh : ${FEATURE_NAME}Event
    data object OnDismissError : ${FEATURE_NAME}Event
}
EOF

cat > "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation/${FEATURE_NAME}ViewModel.kt" <<EOF
package $PACKAGE_BASE.ui.presentation

import $PACKAGE_BASE.domain.usecase.Get${FEATURE_NAME}ListUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ${FEATURE_NAME}ViewModel
    @Inject
    constructor(
        private val get${FEATURE_NAME}ListUseCase: Get${FEATURE_NAME}ListUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(${FEATURE_NAME}UiState())
        val uiState: StateFlow<${FEATURE_NAME}UiState> = _uiState.asStateFlow()

        init {
            refresh()
        }

        fun onEvent(event: ${FEATURE_NAME}Event) {
            when (event) {
                ${FEATURE_NAME}Event.OnRefresh -> refresh()
                ${FEATURE_NAME}Event.OnDismissError -> _uiState.update { it.copy(errorMessage = null) }
            }
        }

        private fun refresh() {
            viewModelScope.launch {
                get${FEATURE_NAME}ListUseCase().collect { items ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }
EOF

cat > "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation/${FEATURE_NAME}Screen.kt" <<EOF
package $PACKAGE_BASE.ui.presentation

import $PACKAGE_BASE.domain.model.${FEATURE_NAME}Model
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ${FEATURE_NAME}Screen(viewModel: ${FEATURE_NAME}ViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ${FEATURE_NAME}ScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun ${FEATURE_NAME}ScreenContent(
    uiState: ${FEATURE_NAME}UiState,
    onEvent: (${FEATURE_NAME}Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.items.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(text = "No items yet", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "Feature name",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.items, key = { it.id }) { item: ${FEATURE_NAME}Model ->
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
EOF

cat > "$BASE_PATH/ui/src/main/kotlin/$KOTLIN_PATH/ui/presentation/navigation/${FEATURE_NAME}Navigation.kt" <<EOF
package $PACKAGE_BASE.ui.presentation.navigation

import $PACKAGE_BASE.ui.presentation.${FEATURE_NAME}Screen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Navigation extension for $FEATURE_NAME feature.
 */
fun NavGraphBuilder.${MODULE_NAME}Screen(navController: NavController) {
    composable("$MODULE_NAME") {
        ${FEATURE_NAME}Screen()
    }
}

/**
 * Navigate to the feature; switch to a type-safe Route if the project uses one.
 */
fun NavController.navigateTo${FEATURE_NAME}() {
    navigate("$MODULE_NAME")
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

Manual wiring still required:
  1. AppDatabase (app module): add ${FEATURE_NAME}Entity to entities list and
     declare:    abstract fun ${MODULE_NAME}Dao(): ${FEATURE_NAME}Dao
  2. DatabaseModule (app module): add
       @Provides
       fun provide${FEATURE_NAME}Dao(db: AppDatabase): ${FEATURE_NAME}Dao = db.${MODULE_NAME}Dao()
  3. Route.kt (core:navigation): add a route for $FEATURE_NAME if you use type-safe navigation.
  4. AppNavGraph: call ${MODULE_NAME}Screen(navController).

Run ./gradlew assembleDebug to verify the scaffold compiles.
EOM