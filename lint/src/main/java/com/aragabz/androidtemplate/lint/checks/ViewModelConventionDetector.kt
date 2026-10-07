package com.aragabz.androidtemplate.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UClass

class ViewModelConventionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            // Anonymous objects (object : ViewModel()) have no name to check.
            val name = node.name ?: return
            val isViewModel = context.evaluator.inheritsFrom(node, "androidx.lifecycle.ViewModel", false)
            if (isViewModel && !name.endsWith("ViewModel")) {
                context.report(
                    ISSUE,
                    node,
                    context.getNameLocation(node),
                    "ViewModel classes should have 'ViewModel' suffix."
                )
            }
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "ViewModelConvention",
            briefDescription = "ViewModel naming convention",
            explanation = "ViewModels should always end with the 'ViewModel' suffix for clarity and consistency.",
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            implementation = Implementation(
                ViewModelConventionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
