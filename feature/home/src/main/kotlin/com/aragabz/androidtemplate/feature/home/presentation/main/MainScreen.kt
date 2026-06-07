package com.aragabz.androidtemplate.feature.home.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
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
import androidx.navigation.NavHostController
import com.aragabz.androidtemplate.feature.home.presentation.home.HomeScreen
import com.aragabz.androidtemplate.feature.todos.presentation.todos.TodosScreen

/**
 * Tab definitions for bottom navigation bar.
 */
enum class MainTab {
    Home,
    Todos,
}

/**
 * Main application screen container with bottom navigation bar.
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.Home,
                    onClick = { selectedTab = MainTab.Home },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == MainTab.Home) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home",
                        )
                    },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.Todos,
                    onClick = { selectedTab = MainTab.Todos },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == MainTab.Todos) Icons.Filled.List else Icons.Outlined.List,
                            contentDescription = "Todos",
                        )
                    },
                    label = { Text("Todos") },
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when (selectedTab) {
                MainTab.Home -> HomeScreen(navController)
                MainTab.Todos -> TodosScreen(navController)
            }
        }
    }
}
