plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.room")
}

android {
    namespace = "com.aragabz.androidtemplate.feature.todos.data"
}

dependencies {
    implementation(project(":feature:todos:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:common"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}
