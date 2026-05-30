package com.aragabz.androidtemplate.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using kotlinx.serialization.
 * Each route represents a destination in the app.
 */
sealed interface Route {
    
    /**
     * Home screen route - main entry point
     */
    @Serializable
    data object Home : Route
    
    /**
     * Details screen route with item ID
     * @param id The ID of the item to display
     */
    @Serializable
    data class Details(val id: String) : Route
}
