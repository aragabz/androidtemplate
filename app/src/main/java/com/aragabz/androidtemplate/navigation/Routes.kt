package com.aragabz.androidtemplate.navigation

import kotlinx.serialization.Serializable

/**
 * Sealed interface representing all navigation routes in the app.
 * Each route is a data object or data class that can be serialized for type-safe navigation.
 */
@Serializable
sealed interface Route {
    /**
     * Home screen route
     */
    @Serializable
    data object Home : Route

    /**
     * Details screen route
     * @param id The ID to display on the details screen
     */
    @Serializable
    data class Details(val id: String) : Route
}
