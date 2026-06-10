#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Check if feature name is provided
if [ -z "$1" ]; then
    echo "Usage: ./create_feature.sh <FeatureName>"
    echo "Example: ./create_feature.sh Profile"
    exit 1
fi

FEATURE_NAME=$1
# Lowercase for module name
MODULE_NAME=$(echo "$FEATURE_NAME" | tr '[:upper:]' '[:lower:]')
# Package name
PACKAGE_NAME="com.aragabz.androidtemplate.feature.$MODULE_NAME"
# Base path
BASE_PATH="feature/$MODULE_NAME"
# Source path
SRC_PATH="$BASE_PATH/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME"

echo "🚀 Creating feature module: $MODULE_NAME..."

# 1. Create directory structure
mkdir -p "$SRC_PATH/presentation/navigation"
mkdir -p "$SRC_PATH/presentation/$MODULE_NAME"
mkdir -p "$SRC_PATH/domain/model"
mkdir -p "$SRC_PATH/domain/repository"
mkdir -p "$SRC_PATH/domain/usecase"
mkdir -p "$SRC_PATH/data/repository"
mkdir -p "$SRC_PATH/di"

# 2. Create build.gradle.kts
cat <<EOF > "$BASE_PATH/build.gradle.kts"
plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "$PACKAGE_NAME"
}

dependencies {
    api(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
EOF

# 3. Create Navigation file
cat <<EOF > "$SRC_PATH/presentation/navigation/${FEATURE_NAME}Navigation.kt"
package $PACKAGE_NAME.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aragabz.androidtemplate.core.navigation.Route
import $PACKAGE_NAME.presentation.$MODULE_NAME.${FEATURE_NAME}Screen

/**
 * Navigation extension for $MODULE_NAME feature.
 */
fun NavGraphBuilder.${MODULE_NAME}Screen(navController: NavController) {
    composable<Route.${FEATURE_NAME}> {
        ${FEATURE_NAME}Screen(navController = navController)
    }
}

/**
 * Navigate to $MODULE_NAME screen.
 */
fun NavController.navigateTo${FEATURE_NAME}() {
    navigate(Route.${FEATURE_NAME})
}
EOF

# 4. Create UI Screen boilerplate
cat <<EOF > "$SRC_PATH/presentation/$MODULE_NAME/${FEATURE_NAME}Screen.kt"
package $PACKAGE_NAME.presentation.$MODULE_NAME

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ${FEATURE_NAME}Screen(
    navController: NavController,
    viewModel: ${FEATURE_NAME}ViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${FEATURE_NAME}") },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Welcome to ${FEATURE_NAME} Screen",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
        }
    }
}
EOF

# 5. Create ViewModel
cat <<EOF > "$SRC_PATH/presentation/$MODULE_NAME/${FEATURE_NAME}ViewModel.kt"
package $PACKAGE_NAME.presentation.$MODULE_NAME

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ${FEATURE_NAME}ViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(${FEATURE_NAME}UiState())
    val uiState: StateFlow<${FEATURE_NAME}UiState> = _uiState.asStateFlow()

    fun onEvent(event: ${FEATURE_NAME}Event) {
        when (event) {
            // Handle events
        }
    }
}
EOF

# 6. Create UiState
cat <<EOF > "$SRC_PATH/presentation/$MODULE_NAME/${FEATURE_NAME}UiState.kt"
package $PACKAGE_NAME.presentation.$MODULE_NAME

data class ${FEATURE_NAME}UiState(
    val isLoading: Boolean = false
)
EOF

# 7. Create Event
cat <<EOF > "$SRC_PATH/presentation/$MODULE_NAME/${FEATURE_NAME}Event.kt"
package $PACKAGE_NAME.presentation.$MODULE_NAME

sealed interface ${FEATURE_NAME}Event {
    // Add events here
}
EOF

# 8. Add to settings.gradle.kts
if ! grep -q ":feature:$MODULE_NAME" settings.gradle.kts; then
    echo "include(\":feature:$MODULE_NAME\")" >> settings.gradle.kts
    echo "✅ Added :feature:$MODULE_NAME to settings.gradle.kts"
fi

echo "----------------------------------------------------"
echo "✅ Feature $FEATURE_NAME created successfully!"
echo "----------------------------------------------------"
echo "Next steps:"
echo "1. Add the route to Route.kt:"
echo "   @Serializable"
echo "   data object $FEATURE_NAME : Route"
echo ""
echo "2. Register ${MODULE_NAME}Screen(navController) in your NavHost."
echo "3. Sync Gradle to finish."
echo "----------------------------------------------------"
