package com.aragabz.androidtemplate.lint.checks

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class ViewModelConventionDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = ViewModelConventionDetector()

    override fun getIssues(): List<Issue> = listOf(ViewModelConventionDetector.ISSUE)

    fun testViewModelWithoutSuffixIsReported() {
        lint()
            .files(
                viewModelStub,
                kotlin(
                    """
                    package test.pkg

                    import androidx.lifecycle.ViewModel

                    class TodosPresenter : ViewModel()
                    """,
                ).indented(),
            ).run()
            .expectWarningCount(1)
            .expectContains("ViewModel classes should have 'ViewModel' suffix.")
    }

    fun testIndirectViewModelWithoutSuffixIsReported() {
        lint()
            .files(
                viewModelStub,
                kotlin(
                    """
                    package test.pkg

                    import androidx.lifecycle.ViewModel

                    abstract class BaseViewModel : ViewModel()

                    class Profile : BaseViewModel()
                    """,
                ).indented(),
            ).run()
            .expectWarningCount(1)
            .expectContains("Profile")
    }

    fun testViewModelWithSuffixIsClean() {
        lint()
            .files(
                viewModelStub,
                kotlin(
                    """
                    package test.pkg

                    import androidx.lifecycle.ViewModel

                    class TodosViewModel : ViewModel()
                    """,
                ).indented(),
            ).run()
            .expectClean()
    }

    fun testAnonymousViewModelIsClean() {
        lint()
            .files(
                viewModelStub,
                kotlin(
                    """
                    package test.pkg

                    import androidx.lifecycle.ViewModel

                    fun createForTest(): ViewModel = object : ViewModel() {}
                    """,
                ).indented(),
            ).run()
            .expectClean()
    }

    fun testNonViewModelClassIsClean() {
        lint()
            .files(
                kotlin(
                    """
                    package test.pkg

                    class TodosRepository
                    """,
                ).indented(),
            ).run()
            .expectClean()
    }

    private companion object {
        val viewModelStub: TestFile =
            kotlin(
                """
                package androidx.lifecycle

                abstract class ViewModel
                """,
            ).indented()
    }
}
