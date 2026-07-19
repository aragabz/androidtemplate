package com.aragabz.androidtemplate.core.common.error

import com.aragabz.androidtemplate.core.common.R
import com.aragabz.androidtemplate.core.common.result.AppError
import com.aragabz.androidtemplate.core.common.result.ErrorCategory
import com.aragabz.androidtemplate.core.common.ui.UiText
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorMapperTest {
    @Test
    fun `toAppError maps IOException to NetworkError`() {
        val throwable = IOException("network down")

        val mapped = throwable.toAppError()

        assertTrue(mapped is AppError.NetworkError)
        assertEquals(ErrorCategory.NETWORK, mapped.category)
        assertTrue(mapped.isRetryable)
    }

    @Test
    fun `toUiText maps http 401 to unauthorized message`() {
        val throwable = AppError.HttpError(401, "unauthorized")

        val uiText = throwable.toUiText()

        assertTrue(uiText is UiText.StringResource)
        val resource = uiText as UiText.StringResource
        assertEquals(R.string.error_http_unauthorized, resource.resId)
    }

    @Test
    fun `toErrorRecord includes http code and retryable flag`() {
        val throwable = AppError.HttpError(503, "service unavailable")

        val record = throwable.toErrorRecord()

        assertEquals(ErrorCategory.HTTP, record.category)
        assertEquals(503, record.httpCode)
        assertTrue(record.isRetryable)
        assertEquals("HttpError", record.causeType)
    }
}
