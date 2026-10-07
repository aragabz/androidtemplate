package com.aragabz.androidtemplate.core.network.session

/**
 * Provides auth tokens for network requests.
 * Bound in :app, which can see both the network and the session storage.
 */
fun interface AuthTokenProvider {
    fun getAuthToken(): String?
}
