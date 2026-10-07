package com.aragabz.androidtemplate.lint.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class DirectColorUsageDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = DirectColorUsageDetector()

    override fun getIssues(): List<Issue> = listOf(DirectColorUsageDetector.ISSUE)

    fun testComposeColorConstructorIsReported() {
        lint()
            .files(
                composeColorStub,
                kotlin(
                    """
                    package test.pkg

                    import androidx.compose.ui.graphics.Color

                    val brand = Color(0xFF6200EE)
                    """,
                ).indented(),
            ).run()
            .expectErrorCount(1)
            .expectContains("Use MaterialTheme.colorScheme or LocalSemanticColors.current instead of a raw Color.")
    }

    fun testAndroidGraphicsColorIsReported() {
        lint()
            .files(
                kotlin(
                    """
                    package test.pkg

                    import android.graphics.Color

                    val red = Color.parseColor("#FF0000")
                    val green = Color.rgb(0, 255, 0)
                    """,
                ).indented(),
            ).run()
            .expectErrorCount(2)
    }

    fun testPaletteFileColorKtIsAllowed() {
        lint()
            .files(
                composeColorStub,
                kotlin(
                    "src/main/kotlin/test/pkg/Color.kt",
                    """
                    package test.pkg

                    import androidx.compose.ui.graphics.Color

                    val Purple40 = Color(0xFF6650A4)
                    """,
                ).indented(),
            ).run()
            .expectClean()
    }

    fun testUnrelatedColorFunctionIsClean() {
        lint()
            .files(
                kotlin(
                    """
                    package test.pkg

                    fun Color(value: Long): Long = value

                    val notACompose = Color(0xFF000000)
                    """,
                ).indented(),
            ).run()
            .expectClean()
    }

    private companion object {
        val composeColorStub: TestFile =
            kotlin(
                """
                package androidx.compose.ui.graphics

                @JvmInline
                value class Color(val value: ULong)

                fun Color(color: Long): Color = Color(color.toULong())
                """,
            ).indented()
    }
}
