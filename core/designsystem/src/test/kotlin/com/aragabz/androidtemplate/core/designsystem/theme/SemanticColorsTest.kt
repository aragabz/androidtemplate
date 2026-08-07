package com.aragabz.androidtemplate.core.designsystem.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SemanticColorsTest {
    @Test
    fun `light semantic colors have correct values`() {
        val colors = LightSemanticColors

        // Success colors
        assertEquals(SuccessLight, colors.success)
        assertEquals(OnSuccessLight, colors.onSuccess)
        assertEquals(SuccessContainerLight, colors.successContainer)
        assertEquals(OnSuccessContainerLight, colors.onSuccessContainer)

        // Warning colors
        assertEquals(WarningLight, colors.warning)
        assertEquals(OnWarningLight, colors.onWarning)
        assertEquals(WarningContainerLight, colors.warningContainer)
        assertEquals(OnWarningContainerLight, colors.onWarningContainer)

        // Info colors
        assertEquals(InfoLight, colors.info)
        assertEquals(OnInfoLight, colors.onInfo)
        assertEquals(InfoContainerLight, colors.infoContainer)
        assertEquals(OnInfoContainerLight, colors.onInfoContainer)
    }

    @Test
    fun `dark semantic colors have correct values`() {
        val colors = DarkSemanticColors

        // Success colors
        assertEquals(SuccessDark, colors.success)
        assertEquals(OnSuccessDark, colors.onSuccess)
        assertEquals(SuccessContainerDark, colors.successContainer)
        assertEquals(OnSuccessContainerDark, colors.onSuccessContainer)

        // Warning colors
        assertEquals(WarningDark, colors.warning)
        assertEquals(OnWarningDark, colors.onWarning)
        assertEquals(WarningContainerDark, colors.warningContainer)
        assertEquals(OnWarningContainerDark, colors.onWarningContainer)

        // Info colors
        assertEquals(InfoDark, colors.info)
        assertEquals(OnInfoDark, colors.onInfo)
        assertEquals(InfoContainerDark, colors.infoContainer)
        assertEquals(OnInfoContainerDark, colors.onInfoContainer)
    }

    @Test
    fun `light and dark semantic colors are different`() {
        val light = LightSemanticColors
        val dark = DarkSemanticColors

        // Success colors should differ
        assertNotEquals(light.success, dark.success)
        assertNotEquals(light.onSuccess, dark.onSuccess)
        assertNotEquals(light.successContainer, dark.successContainer)
        assertNotEquals(light.onSuccessContainer, dark.onSuccessContainer)

        // Warning colors should differ
        assertNotEquals(light.warning, dark.warning)
        assertNotEquals(light.onWarning, dark.onWarning)
        assertNotEquals(light.warningContainer, dark.warningContainer)
        assertNotEquals(light.onWarningContainer, dark.onWarningContainer)

        // Info colors should differ
        assertNotEquals(light.info, dark.info)
        assertNotEquals(light.onInfo, dark.onInfo)
        assertNotEquals(light.infoContainer, dark.infoContainer)
        assertNotEquals(light.onInfoContainer, dark.onInfoContainer)
    }

    @Test
    fun `semantic colors data class has 12 properties`() {
        val colors = LightSemanticColors

        // Verify all properties are accessible
        val properties = listOf(
            colors.success,
            colors.onSuccess,
            colors.successContainer,
            colors.onSuccessContainer,
            colors.warning,
            colors.onWarning,
            colors.warningContainer,
            colors.onWarningContainer,
            colors.info,
            colors.onInfo,
            colors.infoContainer,
            colors.onInfoContainer,
        )

        assertEquals(12, properties.size)
    }

    @Test
    fun `semantic colors are immutable`() {
        val colors1 = LightSemanticColors
        val colors2 = LightSemanticColors

        // Same instance should be returned (immutable)
        assertEquals(colors1, colors2)
    }
}
