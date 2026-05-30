package com.aragabz.androidtemplate.core.common.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

/**
 * Throttles the Flow emissions, only allowing one emission per window duration.
 * @param windowDuration time window in milliseconds
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}

/**
 * Debounces the Flow emissions, only emitting after a period of inactivity.
 * @param timeoutMillis debounce period in milliseconds
 */
fun <T> Flow<T>.debounce(timeoutMillis: Long): Flow<T> = flow {
    var lastValue: T? = null
    var lastEmitTime = 0L

    collect { value ->
        lastValue = value
        val currentTime = System.currentTimeMillis()
        lastEmitTime = currentTime

        delay(timeoutMillis)

        if (currentTime == lastEmitTime && lastValue != null) {
            emit(lastValue!!)
        }
    }
}
