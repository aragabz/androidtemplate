package com.aragabz.androidtemplate.core.common.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for AppResult sealed class and extension functions.
 */
class AppResultTest {
    // region fold() tests

    @Test
    fun `fold handles Success state`() {
        val result: AppResult<String> = AppResult.Success("test data")

        val output = result.fold(
            onSuccess = { "Success: $it" },
            onError = { "Error: ${it.message}" },
            onLoading = { "Loading" },
        )

        assertEquals("Success: test data", output)
    }

    @Test
    fun `fold handles Error state`() {
        val exception = RuntimeException("Something went wrong")
        val result: AppResult<String> = AppResult.Error(exception)

        val output = result.fold(
            onSuccess = { "Success: $it" },
            onError = { "Error: ${it.message}" },
            onLoading = { "Loading" },
        )

        assertEquals("Error: Something went wrong", output)
    }

    @Test
    fun `fold handles Loading state`() {
        val result: AppResult<String> = AppResult.Loading

        val output = result.fold(
            onSuccess = { "Success: $it" },
            onError = { "Error: ${it.message}" },
            onLoading = { "Loading" },
        )

        assertEquals("Loading", output)
    }

    @Test
    fun `fold with different return type`() {
        val result: AppResult<String> = AppResult.Success("42")

        val number: Int = result.fold(
            onSuccess = { it.toInt() },
            onError = { -1 },
            onLoading = { 0 },
        )

        assertEquals(42, number)
    }

    @Test
    fun `fold with complex transformation`() {
        data class User(
            val id: Int,
            val name: String,
        )

        data class UserDto(
            val userId: Int,
            val userName: String,
        )

        val result: AppResult<User> = AppResult.Success(User(1, "Alice"))

        val dto: UserDto? = result.fold(
            onSuccess = { UserDto(it.id, it.name) },
            onError = { null },
            onLoading = { null },
        )

        assertEquals(UserDto(1, "Alice"), dto)
    }

    @Test
    fun `fold exhaustive pattern matching prevents missing cases`() {
        // This test verifies that fold forces handling all cases
        // If we add a new state to AppResult, this will fail to compile
        val results = listOf<AppResult<String>>(
            AppResult.Success("data"),
            AppResult.Error(RuntimeException("error")),
            AppResult.Loading,
        )

        val outputs = results.map { result ->
            result.fold(
                onSuccess = { "success" },
                onError = { "error" },
                onLoading = { "loading" },
            )
        }

        assertEquals(listOf("success", "error", "loading"), outputs)
    }

    // endregion

    // region Property tests

    @Test
    fun `isSuccess returns true for Success`() {
        val result: AppResult<String> = AppResult.Success("data")
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
    }

    @Test
    fun `isError returns true for Error`() {
        val result: AppResult<String> = AppResult.Error(RuntimeException())
        assertFalse(result.isSuccess)
        assertTrue(result.isError)
        assertFalse(result.isLoading)
    }

    @Test
    fun `isLoading returns true for Loading`() {
        val result: AppResult<String> = AppResult.Loading
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
        assertTrue(result.isLoading)
    }

    // endregion

    // region getOrNull tests

    @Test
    fun `getOrNull returns data for Success`() {
        val result: AppResult<String> = AppResult.Success("test")
        assertEquals("test", result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for Error`() {
        val result: AppResult<String> = AppResult.Error(RuntimeException())
        assertNull(result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for Loading`() {
        val result: AppResult<String> = AppResult.Loading
        assertNull(result.getOrNull())
    }

    // endregion

    // region getOrDefault tests

    @Test
    fun `getOrDefault returns data for Success`() {
        val result: AppResult<String> = AppResult.Success("test")
        assertEquals("test", result.getOrDefault("default"))
    }

    @Test
    fun `getOrDefault returns default for Error`() {
        val result: AppResult<String> = AppResult.Error(RuntimeException())
        assertEquals("default", result.getOrDefault("default"))
    }

    @Test
    fun `getOrDefault returns default for Loading`() {
        val result: AppResult<String> = AppResult.Loading
        assertEquals("default", result.getOrDefault("default"))
    }

    // endregion

    // region map tests

    @Test
    fun `map transforms Success data`() {
        val result: AppResult<Int> = AppResult.Success(5)
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Success(10), mapped)
    }

    @Test
    fun `map preserves Error`() {
        val exception = RuntimeException("error")
        val result: AppResult<Int> = AppResult.Error(exception)
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Error(exception), mapped)
    }

    @Test
    fun `map preserves Loading`() {
        val result: AppResult<Int> = AppResult.Loading
        val mapped = result.map { it * 2 }
        assertEquals(AppResult.Loading, mapped)
    }

    // endregion

    // region Chaining tests

    @Test
    fun `onSuccess onError onLoading can be chained`() {
        var successCalled = false
        var errorCalled = false
        var loadingCalled = false

        val result: AppResult<String> = AppResult.Success("data")

        result
            .onSuccess { successCalled = true }
            .onError { errorCalled = true }
            .onLoading { loadingCalled = true }

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertFalse(loadingCalled)
    }

    @Test
    fun `onError is called for Error state`() {
        var errorCalled = false
        var capturedMessage: String? = null

        val result: AppResult<String> = AppResult.Error(RuntimeException("test error"))

        result.onError {
            errorCalled = true
            capturedMessage = it.message
        }

        assertTrue(errorCalled)
        assertEquals("test error", capturedMessage)
    }

    @Test
    fun `onLoading is called for Loading state`() {
        var loadingCalled = false

        val result: AppResult<String> = AppResult.Loading

        result.onLoading { loadingCalled = true }

        assertTrue(loadingCalled)
    }

    // endregion

    // region Real-world usage examples

    @Test
    fun `fold for UI state mapping`() {
        data class UiState(
            val message: String,
            val isLoading: Boolean,
        )

        fun mapToUiState(result: AppResult<String>): UiState =
            result.fold(
                onSuccess = { UiState("Data loaded: $it", false) },
                onError = { UiState("Error: ${it.message}", false) },
                onLoading = { UiState("Loading...", true) },
            )

        val successState = mapToUiState(AppResult.Success("user data"))
        assertEquals(UiState("Data loaded: user data", false), successState)

        val errorState = mapToUiState(AppResult.Error(RuntimeException("network error")))
        assertEquals(UiState("Error: network error", false), errorState)

        val loadingState = mapToUiState(AppResult.Loading)
        assertEquals(UiState("Loading...", true), loadingState)
    }

    @Test
    fun `fold for logging with side effects`() {
        val logs = mutableListOf<String>()

        fun logResult(result: AppResult<String>) {
            result.fold(
                onSuccess = { logs.add("Success: $it") },
                onError = { logs.add("Error: ${it.message}") },
                onLoading = { logs.add("Loading") },
            )
        }

        logResult(AppResult.Success("data"))
        logResult(AppResult.Error(RuntimeException("error")))
        logResult(AppResult.Loading)

        assertEquals(
            listOf("Success: data", "Error: error", "Loading"),
            logs,
        )
    }

    @Test
    fun `fold for converting to nullable with default`() {
        fun <T> AppResult<T>.toNullableOrDefault(default: T): T =
            fold(
                onSuccess = { it },
                onError = { default },
                onLoading = { default },
            )

        assertEquals("data", AppResult.Success("data").toNullableOrDefault("default"))
        assertEquals("default", AppResult.Error(RuntimeException()).toNullableOrDefault("default"))
        assertEquals("default", AppResult.Loading.toNullableOrDefault("default"))
    }

    // endregion
}
