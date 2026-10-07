package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ErrorHandlerTest {
    @Test
    fun `withErrorHandling returns Success for successful operation`() =
        runTest {
            val result = withErrorHandling {
                "success"
            }

            assertTrue(result is AppResult.Success)
            assertEquals("success", (result as AppResult.Success).data)
        }

    @Test
    fun `withErrorHandling returns Error when exception is thrown`() =
        runTest {
            val result = withErrorHandling {
                throw IOException("Network error")
            }

            assertTrue(result is AppResult.Error)
        }

    @Test
    fun `withErrorHandling with dispatcher returns Success`() =
        runTest {
            val result = withErrorHandling(Dispatchers.Default) {
                42
            }

            assertTrue(result is AppResult.Success)
            assertEquals(42, (result as AppResult.Success).data)
        }

    @Test
    fun `withErrorHandling with dispatcher returns Error on exception`() =
        runTest {
            val result = withErrorHandling(Dispatchers.Default) {
                throw IllegalArgumentException("Invalid input")
            }

            assertTrue(result is AppResult.Error)
        }

    @Test
    fun `withErrorHandling handles suspend functions`() =
        runTest {
            val result = withErrorHandling {
                kotlinx.coroutines.delay(10)
                "delayed success"
            }

            assertTrue(result is AppResult.Success)
            assertEquals("delayed success", (result as AppResult.Success).data)
        }

    @Test
    fun `withErrorHandling does not turn coroutine cancellation into an Error`() =
        runTest {
            var result: AppResult<Unit>? = null
            val job = launch { result = withErrorHandling { delay(1_000) } }
            runCurrent()

            job.cancel()
            job.join()

            assertNull(result)
        }

    @Test
    fun `withErrorHandling rethrows CancellationException`() =
        runTest {
            val thrown = runCatching { withErrorHandling { throw CancellationException("cancelled") } }

            assertTrue(thrown.exceptionOrNull() is CancellationException)
        }
}
