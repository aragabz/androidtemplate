package com.aragabz.androidtemplate.core.testing

import app.cash.turbine.ReceiveTurbine

/**
 * Skips items until one matches [predicate] and returns it. Use it for state built by `combine`, where the
 * intermediate states between two meaningful ones depend on scheduling.
 */
suspend fun <T> ReceiveTurbine<T>.awaitItemMatching(predicate: (T) -> Boolean): T {
    var item = awaitItem()
    while (!predicate(item)) item = awaitItem()
    return item
}
