package com.aragabz.androidtemplate.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavHostController
import com.aragabz.androidtemplate.R
import com.aragabz.androidtemplate.core.designsystem.theme.AppTheme
import com.aragabz.androidtemplate.feature.home.presentation.home.HomeScreen
import com.aragabz.androidtemplate.feature.home.presentation.navigation.navigateToEmptyDemo
import com.aragabz.androidtemplate.feature.home.presentation.navigation.navigateToErrorDemo
import com.aragabz.androidtemplate.feature.profile.ui.presentation.ProfileScreen
import com.aragabz.androidtemplate.feature.settings.ui.presentation.SettingsScreen
import com.aragabz.androidtemplate.feature.settings.ui.presentation.navigation.navigateToLanguageSettings
import com.aragabz.androidtemplate.feature.settings.ui.presentation.navigation.navigateToThemeSettings
import com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation.navigateToAddTodo
import com.aragabz.androidtemplate.feature.todos.ui.presentation.navigation.navigateToTodoDetails
import com.aragabz.androidtemplate.feature.todos.ui.presentation.todos.TodosScreen

/**
 * Tabs of the bottom navigation bar.
 */
enum class MainTab(
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Home(R.string.main_tab_home, Icons.Filled.Home, Icons.Outlined.Home),
    Todos(R.string.main_tab_todos, Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List),
    Profile(R.string.main_tab_profile, Icons.Filled.Person, Icons.Outlined.Person),
    Settings(R.string.main_tab_settings, Icons.Filled.Settings, Icons.Outlined.Settings),
}

/**
 * App shell shown after sign-in: a bottom navigation bar hosting each feature's top-level screen.
 * Screens a tab opens are pushed onto the app's [navController], above the shell.
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    MainScreenContent(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        modifier = modifier,
    ) { tab ->
        when (tab) {
            MainTab.Home ->
                HomeScreen(
                    onOpenEmptyDemo = navController::navigateToEmptyDemo,
                    onOpenErrorDemo = navController::navigateToErrorDemo,
                )
            MainTab.Todos ->
                TodosScreen(
                    onAddTodo = navController::navigateToAddTodo,
                    onTodoClick = navController::navigateToTodoDetails,
                )
            MainTab.Profile -> ProfileScreen(onSignOut = onSignOut)
            MainTab.Settings ->
                SettingsScreen(
                    onThemeClick = navController::navigateToThemeSettings,
                    onLanguageClick = navController::navigateToLanguageSettings,
                )
        }
    }
}

@Composable
internal fun MainScreenContent(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
    tabContent: @Composable (MainTab) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    val selected = selectedTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = stringResource(id = tab.labelRes),
                            )
                        },
                        label = { Text(stringResource(id = tab.labelRes)) },
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            tabContent(selectedTab)
        }
    }
}

@PreviewLightDark
@Composable
private fun MainScreenContentPreview() {
    AppTheme {
        MainScreenContent(selectedTab = MainTab.Todos, onTabSelected = {}) { tab ->
            Text(text = stringResource(id = tab.labelRes))
        }
    }
}
