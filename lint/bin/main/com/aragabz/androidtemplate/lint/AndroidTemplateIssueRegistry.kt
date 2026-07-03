package com.aragabz.androidtemplate.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.aragabz.androidtemplate.lint.checks.DirectColorUsageDetector
import com.aragabz.androidtemplate.lint.checks.ViewModelConventionDetector

class AndroidTemplateIssueRegistry : IssueRegistry() {
    override val issues = listOf(
        DirectColorUsageDetector.ISSUE,
        ViewModelConventionDetector.ISSUE
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12 // works with older lint versions

    override val vendor: Vendor = Vendor(
        vendorName = "AndroidTemplate",
        feedbackUrl = "https://github.com/aragabz/androidtemplate/issues",
        contact = "https://github.com/aragabz/androidtemplate"
    )
}
