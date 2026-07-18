# Keep application entry points and generated code used by the template.
-keep class com.aragabz.androidtemplate.** { *; }

# Kotlin and Compose metadata are consumed at runtime by tooling and reflection-based APIs.
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

# Hilt-generated classes and annotations.
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Room entities, DAOs, and generated implementations.
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Retrofit service interfaces and Kotlin serialization support.
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp and logging interceptor are safe to shrink, but suppress missing debug-only warnings.
-dontwarn okhttp3.**

# WorkManager and its annotations.
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Timber tree discovery and custom logging implementations.
-keep class timber.log.** { *; }

# Baseline profile and startup tooling.
-keep class androidx.baselineprofile.** { *; }