package com.aragabz.androidtemplate.lint.checks

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class DirectColorUsageDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("Color", "parseColor", "rgb", "argb")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val fileName = context.file.name
        if (fileName == "Color.kt") return

        val evaluator = context.evaluator
        val isComposeColor = evaluator.isMemberInClass(method, "androidx.compose.ui.graphics.ColorKt") ||
                evaluator.isMemberInClass(method, "androidx.compose.ui.graphics.Color")
        val isAndroidColor = evaluator.isMemberInClass(method, "android.graphics.Color")

        if (isComposeColor || isAndroidColor) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Use MaterialTheme.colorScheme or LocalSemanticColors.current instead of a raw Color."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DirectColorUsage",
            briefDescription = "Direct Color usage",
            explanation = "Directly using Color constants or constructors bypasses the design system. " +
                "Inside AppTheme, use MaterialTheme.colorScheme for Material roles or LocalSemanticColors.current " +
                "for success/warning/info; new palette values belong in core/designsystem theme/Color.kt.",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                DirectColorUsageDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
