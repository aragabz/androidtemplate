package com.aragabz.androidtemplate.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class benchmarks the performance of the app.
 * It compares the startup and scrolling performance with and without baseline profiles.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmarks {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() = startup(CompilationMode.Partial(
        baselineProfileMode = BaselineProfileMode.Require
    ))

    @Test
    fun scrollBenchmark() = benchmarkRule.measureRepeated(
        packageName = "com.aragabz.androidtemplate",
        metrics = listOf(androidx.benchmark.macro.FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Require
        ),
        startupMode = StartupMode.WARM,
        iterations = 5,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Find a scrollable list and scroll it
        val list = device.findObject(androidx.test.uiautomator.By.scrollable(true))
        if (list != null) {
            list.setGestureMargin(device.displayWidth / 5)
            list.fling(androidx.test.uiautomator.Direction.DOWN)
        }
    }

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.aragabz.androidtemplate",
        metrics = listOf(androidx.benchmark.macro.StartupTimingMetric()),
        compilationMode = compilationMode,
        startupMode = StartupMode.COLD,
        iterations = 5,
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndWait()
    }
}
