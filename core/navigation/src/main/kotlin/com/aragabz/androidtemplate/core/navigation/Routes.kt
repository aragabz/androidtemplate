package com.aragabz.androidtemplate.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using kotlinx.serialization.
 * Each route represents a destination in the app.
 */
sealed interface Route {
    
    /**
     * Splash screen route - entry point
     */
    @Serializable
    data object Splash : Route

    /**
     * Main screen route - bottom navigation bar container
     */
    @Serializable
    data object Main : Route

    /**
     * Login screen route - authentication entry point
     */
    @Serializable
    data object Login : Route
    
    /**
     * Register screen route - user registration
     */
    @Serializable
    data object Register : Route
    
    /**
     * Home screen route - empty dashboard home tab
     */
    @Serializable
    data object Home : Route

    /**
     * Todos screen route - todos list tab
     */
    @Serializable
    data object Todos : Route

    /**
     * Settings screen route - settings tab
     */
    @Serializable
    data object Settings : Route
    
    /**
     * Details screen route with item ID
     * @param id The ID of the item to display
     */
    @Serializable
    data class Details(val id: String) : Route
    
    /**
     * Profile screen route - user profile
     */
    @Serializable
    data object Profile : Route
    
    /**
     * Edit profile screen route
     */
    @Serializable
    data object EditProfile : Route

    /**
     * Add todo screen route - create new todo
     */
    @Serializable
    data object AddTodo : Route

    /**
     * Todo details screen route - view/edit todo
     * @param id The ID of the todo to display
     */
    @Serializable
    data class TodoDetails(val id: String) : Route
}

