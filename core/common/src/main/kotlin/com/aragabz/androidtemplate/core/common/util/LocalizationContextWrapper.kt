package com.aragabz.androidtemplate.core.common.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

/**
 * A [ContextWrapper] that overrides the locale of the context.
 * Used for runtime language switching.
 */
class LocalizationContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        /**
         * Wraps the given [Context] with a new locale.
         * @param context The context to wrap.
         * @param language The language code (e.g., "en", "ar").
         * @return A new context with the overridden locale.
         */
        fun wrap(context: Context, language: String): Context {
            val locale = Locale.forLanguageTag(language)
            Locale.setDefault(locale)

            val resources = context.resources
            val configuration = Configuration(resources.configuration)

            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            configuration.setLocales(localeList)

            return context.createConfigurationContext(configuration)
        }
    }
}
