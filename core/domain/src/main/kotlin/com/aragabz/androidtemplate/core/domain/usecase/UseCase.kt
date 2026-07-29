package com.aragabz.androidtemplate.core.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Base class for all Use Cases (Interactors).
 *
 * @param P the type of input parameters.
 * @param R the type of the result.
 */
abstract class UseCase<in P, out R>(
    private val dispatcher: CoroutineDispatcher,
) {
    /**
     * Executes the use case on the provided dispatcher.
     */
    suspend operator fun invoke(parameters: P): R =
        withContext(dispatcher) {
            execute(parameters)
        }

    /**
     * Implementation of the business logic.
     */
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * A use case that doesn't take any input parameters.
 */
abstract class NoParamUseCase<out R>(
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): R =
        withContext(dispatcher) {
            execute()
        }

    protected abstract suspend fun execute(): R
}

/**
 * Base class for Use Cases that return a [Flow].
 */
abstract class FlowUseCase<in P, out R>(
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(parameters: P): Flow<R> =
        execute(parameters)
            .flowOn(dispatcher)

    protected abstract fun execute(parameters: P): Flow<R>
}

/**
 * A Flow use case that doesn't take any input parameters.
 */
abstract class NoParamFlowUseCase<out R>(
    private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(): Flow<R> =
        execute()
            .flowOn(dispatcher)

    protected abstract fun execute(): Flow<R>
}
