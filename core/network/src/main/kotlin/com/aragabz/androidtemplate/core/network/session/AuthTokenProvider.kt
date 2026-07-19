package com.aragabz.androidtemplate.core.network.session

/**
 * Provides auth tokens for network requests.
 */
interface AuthTokenProvider {
    fun getAuthToken(): String?
}
