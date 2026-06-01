package com.aragabz.androidtemplate.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Wraps the Flow emissions in AppResult, emitting Loading first, then Success/Error.
 */
fun <T> Flow<T>.asResult(): Flow<AppResult<T>> =
    this
        .map<T, AppResult<T>> { AppResult.Success(it) }
        .onStart { emit(AppResult.Loading) }
        .catch { emit(AppResult.Error(it)) }
