package com.aragabz.androidtemplate.core.ui.text

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UiTextTest {
    @Test
    fun `string resources with same id and args are equal`() {
        val first = UiText.StringResource(1, "a", 2)
        val second = UiText.StringResource(1, "a", 2)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `string resources with different args are not equal`() {
        assertNotEquals(UiText.StringResource(1, "a"), UiText.StringResource(1, "b"))
        assertNotEquals(UiText.StringResource(1), UiText.StringResource(2))
    }
}
